package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.R
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.databinding.ActivityUserProfileBinding
import com.example.myapplication.utils.StaticAddress
import android.content.Context
import android.opengl.Visibility
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.adapters.MyProfileProductAdapter
import com.example.myapplication.models.MyProfileProduct
import java.util.HashMap

class UserProfileActivity : AppCompatActivity() {

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var binding: ActivityUserProfileBinding
    private var follow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isProfileMine = intent.getBooleanExtra("MyProfile", false)
        val companyId = intent.getLongExtra("companyId", 0)

        if(isProfileMine)
            binding.goBack.visibility = View.GONE

        binding.goBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        if(isProfileMine) {
            binding.goToCartCard.visibility = View.GONE
            binding.gridLikeProductCard.visibility = View.GONE
            binding.gridUnlikeProductCard.visibility = View.GONE
        }
        else {
            binding.goToCartCard.visibility = View.VISIBLE
            binding.gridLikeProductCard.visibility = View.VISIBLE
            binding.gridUnlikeProductCard.visibility = View.VISIBLE

            isProfileLiked(companyId)
            //Toast.makeText(this, "FOLLOW JE " + follow.toString(), Toast.LENGTH_LONG).show()

            binding.gridLikeProductCard.setOnClickListener {
                binding.gridLikeProductCard.visibility = View.GONE
                binding.gridUnlikeProductCard.visibility = View.VISIBLE
                followCompany(companyId)
            }

            binding.gridUnlikeProductCard.setOnClickListener {
                binding.gridLikeProductCard.visibility = View.VISIBLE
                binding.gridUnlikeProductCard.visibility = View.GONE
                unfollowCompany(companyId)
            }
        }

        if(isProfileMine) {
            getMyNumbers(0)
            fetchProductData(0)
        }
        else {
            getMyNumbers(companyId)
            fetchProductData(companyId)
        }

        try {
            @SuppressLint("MissingInflatedId", "LocalSuppress")
            val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
            bottomNav.add(MeowBottomNavigation.Model(home,R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search,R.drawable.search_black_24dp__1_))
            bottomNav.add(MeowBottomNavigation.Model(add,R.drawable.round_add_24))
            bottomNav.add(MeowBottomNavigation.Model(notf,R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account,R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {

                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    add-> {
                        val intent   = Intent(this, AddPayementMethod::class.java)
                        startActivity(intent)
                    }
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    account-> {
                        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val role = sharedPreferences.getString("role", "empty")
                        if((role.equals("Kupac") || role.equals("Dostavljač")))
                            startActivity(Intent(this, UserAccountActivity::class.java))
                        else {
                            val intent   = Intent(this, UserProfileActivity::class.java)
                            intent.putExtra("MyProfile", true)
                            startActivity(intent)
                        }
                    }
                }
            }

            if (isProfileMine)
                bottomNav.show(account)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }

    private fun getMyNumbers(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/numberInfo/$companyId"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                binding.numOfProducts.text = response.getString("numOfProducts")
                binding.numOfSales.text = response.getString("numOfSales")
                binding.numOfFollowers.text = response.getString("numOfFollowers")
                binding.fullName.text = response.getString("companyName")
                binding.email.text = response.getString("email")
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    private fun fetchProductData(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/product/productList/$companyId/1/10"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val productList: MutableList<MyProfileProduct> = ArrayList()
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    productList.add(
                        MyProfileProduct(objekat.getLong("id"),
                            objekat.getString("name"),
                            objekat.getDouble("price"),
                            objekat.getString("description"),
                            companyId)
                    )
                }

                val products = productList
                val adapter = MyProfileProductAdapter(this, R.layout.one_product_my_profile_layout, products)
                val listView: ListView = findViewById(R.id.productsListView)
                listView.adapter = adapter
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)

    }
    private fun isProfileLiked(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/following/doIfollow/$companyId"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                if(response.getString("Message").equals("true")) {
                    binding.gridLikeProductCard.visibility = View.GONE
                    binding.gridUnlikeProductCard.visibility = View.VISIBLE
                }
                else {
                    binding.gridLikeProductCard.visibility = View.VISIBLE
                    binding.gridUnlikeProductCard.visibility = View.GONE
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    private fun followCompany(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/following/addFollow/$companyId"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Toast.makeText(this, "Zapratili ste korisnika.", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }

    private fun unfollowCompany(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/following/deleteFollow/$companyId"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                Toast.makeText(this, "Otpratili ste korisnika.", Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }
}

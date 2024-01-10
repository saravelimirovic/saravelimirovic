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
import android.graphics.BitmapFactory
import android.graphics.Color
import android.opengl.Visibility
import android.util.Base64
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.marginRight
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.adapters.MyProfileProductAdapter
import com.example.myapplication.models.MyProfileProduct
import java.util.*
import kotlin.collections.ArrayList

class UserProfileActivity : AppCompatActivity() {

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var binding: ActivityUserProfileBinding
    private var myCompanyId = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyCompanyId()

        val companyId = intent.getLongExtra("companyId", 0)

        if(myCompanyId.equals(companyId))
            binding.goBack.visibility = View.GONE

        binding.goBack.setOnClickListener {
            finish()
        }

        binding.goToCart.setOnClickListener {
            val intent = Intent(this, UserOrderDetails::class.java)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }

        binding.allCategories.setOnClickListener {
            if(myCompanyId.equals(companyId)) {
                fetchProductData(0, "SVE")
            }
            else {
                fetchProductData(companyId, "SVE")
            }
            val greenColor = ContextCompat.getColor(this, R.color.green_primary)
            binding.allCategories.setTextColor(greenColor)

            val container = findViewById<LinearLayout>(R.id.categoryContainer)

            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)

                if (child is TextView) {
                    val textView = child as TextView
                    textView.setTextColor(Color.BLACK)
                }
            }
        }

        if(myCompanyId.equals(companyId)) {
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

        if(myCompanyId.equals(companyId)) {
            getMyCategories(0)
            getMyNumbers(0)
            fetchProductData(0, "SVE")
        }
        else {
            getMyCategories(companyId)
            getMyNumbers(companyId)
            fetchProductData(companyId, "SVE")
        }

        try {
            @SuppressLint("MissingInflatedId", "LocalSuppress")
            val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
            bottomNav.add(MeowBottomNavigation.Model(home,R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search,R.drawable.search_black_24dp__1_))

            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val role = sharedPreferences.getString("role", "empty")

            if (role == "Dostavljač" || role == "Prodavac") {
                bottomNav.add(MeowBottomNavigation.Model(add, R.drawable.round_add_24))
            }

            bottomNav.add(MeowBottomNavigation.Model(notf,R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account,R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    account -> {
                        if (role == "Prodavac") {
                            startActivity(Intent(this, UserProfileActivity::class.java))
                        } else {
                            val intent = Intent(this, UserAccountActivity::class.java)
                            intent.putExtra("MyProfile", true)
                            startActivity(intent)
                        }
                    }
                    add -> {
                        if (role == "Dostavljač") {
                            val intent = Intent(this, EnterRouteActivity::class.java)
                            startActivity(intent)
                        }
                        if (role != "Dostavljač" && role != "Kupac") {
                            val intent = Intent(this, AddProductActivity::class.java)
                            startActivity(intent)
                        }
                    }
                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }

            }

            if(myCompanyId.equals(companyId))
                bottomNav.show(account)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }

    private fun getMyCategories(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/category/$companyId"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if(response.length() == 0) {
                    val noCreditCards: RelativeLayout = findViewById(R.id.noProducts)
                    noCreditCards.visibility = View.VISIBLE
                }
                else {
                    val container = findViewById<LinearLayout>(R.id.categoryContainer)

                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    )
                    layoutParams.setMargins(0, 0, 16, 0)
                    layoutParams.height = 45

                    var selectedTextView: TextView? = null
                    for (i in 0 until response.length()) {
                        val textView = TextView(this)
                        textView.text = setCategoryName(response[i].toString())
                        val categoryBackend = response[i].toString()
                        var textLowerCase = categoryBackend
                        textView.setOnClickListener {
                            selectedTextView?.setTextColor(Color.BLACK)

                            selectedTextView = textView

                            val greenColor = ContextCompat.getColor(this, R.color.green_primary)
                            textView.setTextColor(greenColor)

                            getProductsInCategory(companyId, textLowerCase as String)
                        }
                        textView.layoutParams = layoutParams
                        textView.gravity = Gravity.CENTER
                        textView.layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
                        textView.setPadding(0, 0, 10, 0)
                        textView.height = ViewGroup.LayoutParams.MATCH_PARENT
                        textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12F)
                        textView.text = textView.text.toString().toUpperCase(Locale.getDefault())

                        container.addView(textView)
                    }
                }
            },
            { error ->
                val noCreditCards: RelativeLayout = findViewById(R.id.noProducts)
                noCreditCards.visibility = View.VISIBLE
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

    private fun setCategoryName(categoryBackend: String): String {
        if(categoryBackend.equals("freshFruits"))
            return "sveže voće"
        else if(categoryBackend.equals("driedFruits"))
            return "sušeno voće"
        else if(categoryBackend.equals("vegetables"))
            return "povrće"
        else if(categoryBackend.equals("meatProducts"))
            return "meso"
        else if(categoryBackend.equals("dairyProducts"))
            return "mlečni proizvodi"
        else if(categoryBackend.equals("organicProducts"))
            return "organski proizvodi"
        else if(categoryBackend.equals("spices"))
            return "začini"
        else if(categoryBackend.equals("herbs"))
            return "sveže bilje"
        else if(categoryBackend.equals("flowersAndSeedlings"))
            return "semenke"
        else if(categoryBackend.equals("beeProducts"))
            return "pčelinji proizvodi"
        else if(categoryBackend.equals("cannedFoods"))
            return "konzervirana hrana"
        else
            return "ručni rad"
    }

    private fun getProductsInCategory(companyId: Long, category: String) {
        fetchProductData(companyId, category)
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

                // Base64 string koji predstavlja sliku
                val base64Image = response.getString("logo")

                // Dekodiranje Base64 stringa u Bitmap
                val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                // Postavljanje dekodiranog Bitmap-a u ImageView
                binding.profileImage.setImageBitmap(decodedBitmap)
            },
            { error ->
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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

    private fun fetchProductData(companyId: Long, category: String) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/product/productList/$companyId/$category/1/10"

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
                            companyId,
                            objekat.getString("image"))
                    )
                }

                val products = productList
                val adapter = MyProfileProductAdapter(this, R.layout.one_product_my_profile_layout, products)
                val listView: ListView = findViewById(R.id.productsListView)
                listView.adapter = adapter
            },
            { error ->
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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

    public fun getMyCompanyId() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/myCompany"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                myCompanyId = response.getLong("id")
            },
            { error ->
                myCompanyId = 0L
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

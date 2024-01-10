package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.adapters.AvailableDeliveryManAdapter
import com.example.myapplication.adapters.CreditCardAdapter
import com.example.myapplication.adapters.OrdersAdapter
import com.example.myapplication.databinding.ActivityAvailableDeliverymanBinding
import com.example.myapplication.databinding.ActivityDeliveryRequestsBinding
import com.example.myapplication.models.AvailableDeliveryMan
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.models.Order
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import java.util.Date
import java.util.HashMap

class AvailableDeliverymanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAvailableDeliverymanBinding
    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_available_deliveryman)
        binding = ActivityAvailableDeliverymanBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.goBack.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val to = intent.getStringExtra("to")
        fetchDeliveryMan(to)

//        val availableDeliveryManList: MutableList<AvailableDeliveryMan> = ArrayList()

//        availableDeliveryManList.add(AvailableDeliveryMan("Pera", R.drawable.avatar.toString(), "12-10-2023", "Kragujevac", "Raca", 50.0F))
//        availableDeliveryManList.add(AvailableDeliveryMan("Pera", R.drawable.avatar.toString(), "12-10-2023", "Kragujevac", "Raca", 50.0F))
//        availableDeliveryManList.add(AvailableDeliveryMan("Pera", R.drawable.avatar.toString(), "12-10-2023", "Kragujevac", "Raca", 50.0F))
//        availableDeliveryManList.add(AvailableDeliveryMan("Pera", R.drawable.avatar.toString(), "12-10-2023", "Kragujevac", "Raca", 50.0F))


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById<NavigationView>(R.id.drawer_view)

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "empty")

        if (role == "Dostavljač") {
            navView.menu.removeItem(R.id.kupovina1)
            navView.menu.removeItem(R.id.nalog1)
        }

        if (role == "Prodavac") {
            navView.menu.removeItem(R.id.kupovina2)
            navView.menu.removeItem(R.id.nalog2)
        }
        if (role == "Kupac") {
            navView.menu.removeItem(R.id.kupovina2)
            navView.menu.removeItem(R.id.kupovina1)
            navView.menu.removeItem(R.id.nalog1)
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId){

                R.id.drawer_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }

                R.id.drawer_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                }

                R.id.drawer_notf -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                }

                R.id.drawer_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    finish()
                }

                 R.id.drawer_account1 -> {
                    startActivity(Intent(this, UserAccountActivity::class.java))
                    finish()
                }

                R.id.drawer_account2 -> {
                    startActivity(Intent(this, UserAccountActivity::class.java))
                    finish()
                }

                R.id.drawer_orders -> {
                    startActivity(Intent(this, OrdersActivity::class.java))
                    finish()
                }

                R.id.drawer_delivery_requests -> {
                    startActivity(Intent(this, DeliveryRequests::class.java))
                    finish()
                }

                R.id.drawer_logout -> {
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.remove("JWT_TOKEN")
                    editor.remove("firstName")
                    editor.remove("lastName")
                    editor.remove("email")
                    editor.remove("password")
                    editor.remove("companyAdded")
                    editor.apply()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
            true
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

        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }

    }

    private fun fetchDeliveryMan(to: String?) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/route/getAllDeliveryRequests/$to"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if(response.length() == 0) {
                    binding.noDeliverers.visibility = View.VISIBLE
                    binding.availableDeliveryManListView.visibility = View.GONE
                }
                val deliveryMan: MutableList<AvailableDeliveryMan> = ArrayList()
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    deliveryMan.add(
                        AvailableDeliveryMan(objekat.getString("userImage"),
                            objekat.getLong("userId"),
                            objekat.getString("firstNameUser"),
                            objekat.getString("lastNameUser"),
                            objekat.getString("from"),
                            objekat.getString("to"),
                            objekat.getString("date"),
                            objekat.getString("time"))
                    )
                }

                val deliveries = deliveryMan
                val adapter = AvailableDeliveryManAdapter(this, R.layout.activity_one_available_deliveryman, deliveries)
                val listView: ListView = findViewById(R.id.availableDeliveryManListView)
                listView.adapter = adapter
            },
            { error ->
                binding.noDeliverers.visibility = View.VISIBLE
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
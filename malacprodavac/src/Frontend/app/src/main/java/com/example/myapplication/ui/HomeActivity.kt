package com.example.myapplication.ui

import ProductAdapter
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.adapters.CommentAdapter
import com.example.myapplication.adapters.CompanyAdapterHome
import com.example.myapplication.adapters.ProductHorizontalRecyclerViewAdapter
import com.example.myapplication.models.Comment
import com.example.myapplication.models.CompanyCard
import com.example.myapplication.models.ProductCard
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import java.util.Date

class HomeActivity : AppCompatActivity() {

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var popularProductsRecyclerView: RecyclerView
    private lateinit var adapter: ProductHorizontalRecyclerViewAdapter
    private lateinit var nearByProductsRecyclerView: RecyclerView
    private lateinit var adapter2: ProductHorizontalRecyclerViewAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val firstTime = intent.getBooleanExtra("firstTime", false)
        if (firstTime) {
            val intent = Intent(this, RegisterFinal::class.java)
            startActivity(intent)
        }

        setVisibility()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById<NavigationView>(R.id.drawer_view)

        val menu_button = findViewById<ImageButton>(R.id.menu)

        menu_button.setOnClickListener{
            if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START)
            else
                drawerLayout.closeDrawer(GravityCompat.END)
        }

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
            val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
            bottomNav.add(MeowBottomNavigation.Model(home, R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search, R.drawable.search_black_24dp__1_))

            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val role = sharedPreferences.getString("role", "empty")

            if (role == "Dostavljač" || role == "Prodavac") {
                bottomNav.add(MeowBottomNavigation.Model(add, R.drawable.round_add_24))
            }
            bottomNav.add(MeowBottomNavigation.Model(notf, R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account, R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when (it.id) {
                    search -> {
                        val intent = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    notf -> {
                        val intent = Intent(this, NotificationsActivity::class.java)
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
                }
            }

            bottomNav.show(home)

        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }


        fetchInNearbyCompanies()
        fetchPopularCompanies()
    }

    fun fetchInNearbyCompanies() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/inNearby/1/10"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val companies: MutableList<CompanyCard> = ArrayList()
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    companies.add(
                        CompanyCard(objekat.getLong("id"),
                            objekat.getString("companyName"),
                            objekat.getDouble("totalRate"),
                            objekat.getBoolean("followed"),
                            objekat.getString("logo"),
                        ))
                }

                val companiesList = companies
                val adapter = CompanyAdapterHome(this, companiesList, this)
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                popularProductsRecyclerView = findViewById(R.id.in_nearby_products_recycler_view)
                popularProductsRecyclerView.layoutManager = layoutManager
                popularProductsRecyclerView.adapter = adapter

            },
            { error ->
                val noNearBy: RelativeLayout = findViewById(R.id.noNearBy)
                noNearBy.visibility = View.VISIBLE
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }

    fun fetchPopularCompanies() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/popular/1/10"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val companies: MutableList<CompanyCard> = ArrayList()
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    companies.add(
                        CompanyCard(objekat.getLong("id"),
                            objekat.getString("companyName"),
                            objekat.getDouble("totalRate"),
                            objekat.getBoolean("followed"),
                            objekat.getString("logo")
                        ))
                }

                val companiesList = companies
                val adapter = CompanyAdapterHome(this, companiesList, this)
                val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                popularProductsRecyclerView = findViewById(R.id.popular_products_recycler_view)
                popularProductsRecyclerView.layoutManager = layoutManager
                popularProductsRecyclerView.adapter = adapter

            },
            { error ->
                //Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }

/*
    private fun generateDummyProducts(): List<ProductCard> {
        val products: MutableList<ProductCard> = ArrayList()
        products.add(ProductCard("Svece pepco vanila", 36, 4.5F, true, "slika"))
        products.add(ProductCard("Lidl kifla sa kremom", 149, 3.5F, false, "slika"))
        products.add(ProductCard("Domaci dzem od kupina Katarina", 239, 4.5F, false, "slika"))
        products.add(ProductCard("Domaci puter od kikirija", 199, 4F, false, "slika"))
        products.add(ProductCard("Domaci dzem od kupina Katarina", 239, 4.5F, false, "slika"))
        products.add(ProductCard("Domaci puter od kikirija", 199, 4F, false, "slika"))
        products.add(ProductCard("Domaci dzem od kupina Katarina", 239, 4.5F, false, "slika"))
        products.add(ProductCard("Domaci puter od kikirija", 199, 4F, false, "slika"))
        products.add(ProductCard("Svece ", 56, 5F, true, "slika"))
        return products
    }
*/

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setVisibility() {
        val navView: NavigationView = findViewById(R.id.drawer_view)
        val navMenu: Menu = navView.menu

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "empty")

        val drawerProfileItem: MenuItem? = navMenu.findItem(R.id.drawer_profile)

        if (drawerProfileItem != null) {
            drawerProfileItem.setVisible(!(role.equals("Kupac") || role.equals("Dostavljač")))
        } else {
            Toast.makeText(this, "Stavka ne postoji", Toast.LENGTH_LONG).show()
        }
    }
}
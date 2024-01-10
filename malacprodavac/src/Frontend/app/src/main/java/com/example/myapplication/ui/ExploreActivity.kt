package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.adapters.CompanyAdapterHome
import com.example.myapplication.adapters.CompanyExploreAdapter
import com.example.myapplication.models.CompanyCard
import com.example.myapplication.models.CompanyMap
//import com.example.myapplication.adapters.ProductAdapter
import com.example.myapplication.models.ProductCard
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import org.json.JSONException
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class ExploreActivity : AppCompatActivity() {
    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var CompaniesRecyclerView: RecyclerView
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private var isMap = false
    private var currentSearch: String = "sve"

    @SuppressLint("MissingInflatedId", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)

        setVisibility()
        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        mapView = findViewById(R.id.mapCompanies)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        mapController = mapView.controller
        mapController.setZoom(8.0)

        val lista = findViewById<TextView>(R.id.listOfCompanies)
        val mapa = findViewById<TextView>(R.id.mapOfCompanies)
        val separator = findViewById<ImageView>(R.id.separator)
        val searchView = findViewById<SearchView>(R.id.searchId)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (!newText.equals("")) {
                        if (isMap)
                            fetchCompaniesOnMap(newText)
                        else
                            fetchCompanies(newText)
                        currentSearch = newText
                    }
                    else {
                        if (isMap)
                            fetchCompaniesOnMap("sve")
                        else
                            fetchCompanies("sve")
                    }

                }
                return true
            }
        })

        fetchCompanies("sve") //prvi put kad ode na stranu

        lista.setOnClickListener {
            isMap = false
            fetchCompanies(currentSearch)
            lista.setTextColor(resources.getColor(R.color.green_primary))
            mapa.setTextColor(resources.getColor(R.color.black))
            val params = separator.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(24, 0, 0, 0)
            separator.layoutParams = params
            val companies_map = findViewById<CardView>(R.id.map_companies_layout)
            companies_map.visibility = View.GONE
            val grid = findViewById<RecyclerView>(R.id.explore_grid)
            grid.visibility = View.VISIBLE
        }

        mapa.setOnClickListener {
            isMap = true
            fetchCompaniesOnMap(currentSearch)
            mapa.setTextColor(resources.getColor(R.color.green_primary))
            lista.setTextColor(resources.getColor(R.color.black))
            val params = separator.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(180, 0, 0, 0);
            separator.layoutParams = params
            val companies_map = findViewById<CardView>(R.id.map_companies_layout)
            companies_map.visibility = View.VISIBLE
            val grid = findViewById<RecyclerView>(R.id.explore_grid)
            grid.visibility = View.GONE
        }

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

                    home -> {
                       val intent   = Intent(this, HomeActivity::class.java)
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
                }

            }

            bottomNav.show(search)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }

    fun fetchCompanies(search: String) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/search/$search/1/10"

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
                val adapter = CompanyExploreAdapter(this, companiesList, this)
                CompaniesRecyclerView = findViewById(R.id.explore_grid)
                CompaniesRecyclerView.layoutManager = GridLayoutManager(this, 2)
                CompaniesRecyclerView.adapter = adapter

                val noResults: RelativeLayout = findViewById(R.id.noComapnies)
                noResults.visibility = View.GONE
                val searchResults: RelativeLayout = findViewById(R.id.searchResults)
                searchResults.visibility = View.VISIBLE
            },
            { error ->
                val noResults: RelativeLayout = findViewById(R.id.noComapnies)
                noResults.visibility = View.VISIBLE
                val searchResults: RelativeLayout = findViewById(R.id.searchResults)
                searchResults.visibility = View.GONE
                //Toast.makeText(this@ExploreActivity, "Nema kompanija", Toast.LENGTH_SHORT).show()
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

    private fun updateUI(companies: List<CompanyMap>) {
        mapView.overlays.clear()
        for (company in companies) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(company.latitude, company.longitude)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = company.companyName
            marker.setOnMarkerClickListener { marker, mapView ->
                Toast.makeText(this, marker.title, Toast.LENGTH_SHORT).show()
                val intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("companyId", company.id)
                startActivity(intent)

                true
            }
            mapView.overlays.add(marker)
        }

        val centralCoordinates = GeoPoint(44.0165, 21.0059) // Srbija
        mapController.setCenter(centralCoordinates)
    }

    private fun fetchCompaniesOnMap(search: String) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/searchMap/$search/1/10"

        val jsonObjectRequest = object: JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val noResults: RelativeLayout = findViewById(R.id.noComapnies)
                    noResults.visibility = View.GONE
                    val companies_map = findViewById<CardView>(R.id.map_companies_layout)
                    companies_map.visibility = View.VISIBLE
                    val companies = parseCompanyList(response)
                    updateUI(companies)
                } catch (e: JSONException) {
                    Log.e("Error", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                val noResults: RelativeLayout = findViewById(R.id.noComapnies)
                noResults.visibility = View.VISIBLE
                val companies_map = findViewById<CardView>(R.id.map_companies_layout)
                companies_map.visibility = View.GONE
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

    private fun parseCompanyList(response: JSONArray): List<CompanyMap> {
        val userList = mutableListOf<CompanyMap>()
        for (i in 0 until response.length()) {
            val userJson = response.getJSONObject(i)
            val user = CompanyMap(
                userJson.getLong("id"),
                userJson.getString("companyName"),
                userJson.getDouble("latitude"),
                userJson.getDouble("longitude")
            )
            userList.add(user)
        }
        return userList
    }









}
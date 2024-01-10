package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.utils.StaticAddress
import com.example.myapplication.utils.getRoute
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class ShowRouteActivity : AppCompatActivity() {

    private lateinit var map: MapView

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_route_show)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))


        val lonStart = intent.getDoubleExtra("lonStart", 0.5)
        val latStart = intent.getDoubleExtra("latStart", 0.5)
        val lonEnd = intent.getDoubleExtra("lonEnd", 0.5)
        val latEnd = intent.getDoubleExtra("latEnd", 0.5)
        val cityStart = intent.getStringExtra("cityStart")
        val cityEnd = intent.getStringExtra("cityEnd")
        val date = intent.getStringExtra("date")
        val time = intent.getStringExtra("time")

//        Toast.makeText(this, "$cityStart : $cityEnd - $date : $time", Toast.LENGTH_LONG).show()

        //addRoute(cityStart, cityEnd, date, time)

        map = findViewById(R.id.mapViewRoute)
        map.tileProvider.tileSource = TileSourceFactory.MAPNIK
        map.visibility = android.view.View.VISIBLE

        val center = GeoPoint(44.0165, 21.0059)
        map.controller.setCenter(center)
        map.controller.setZoom(8.0)

        val startMarker = Marker(map)
        startMarker.position = GeoPoint(latStart, lonStart)
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        startMarker.title = "Početak"
        map.overlays.add(startMarker)

        val endMarker = Marker(map)
        endMarker.position = GeoPoint(latEnd, lonEnd)
        endMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        endMarker.title = "Kraj"
        map.overlays.add(endMarker)

        getRoute(GeoPoint(latStart, lonStart), GeoPoint(latEnd, lonEnd)) { routePoints ->
            drawRouteOnMap(routePoints)
        }



//        val buttonAddDateAndTime: Button = findViewById(R.id.addDateAndTimeButton)
//        buttonAddDateAndTime.setOnClickListener {
//            val intent = Intent(this, AddNewRouteDateAndTimeActivity::class.java)
//        }

        val nextStepButton: Button = findViewById(R.id.nextStepButton)
        nextStepButton.setOnClickListener {
            addRoute(cityStart, cityEnd, date, time)
        }



        val goBack = findViewById<Button>(R.id.go_back)
        goBack.setOnClickListener {
            val intent = Intent(this, AddNewRouteDateAndTimeActivity::class.java)
            startActivity(intent)
        }

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
                when(it.id) {
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
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

            bottomNav.show(add)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }

    private fun addRoute(cityStart: String?, cityEnd: String?, date: String?, time: String?) {
        val jsonParams = JSONObject()
        jsonParams.put("cityStart", cityStart)
        jsonParams.put("cityEnd", cityEnd)
        jsonParams.put("date", date)
        jsonParams.put("time", time)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/route/add"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                Toast.makeText(this, "Ruta je dodata.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, EnterRouteActivity::class.java)
                startActivity(intent)
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

    private fun drawRouteOnMap(routePoints: List<GeoPoint>) {
        val routeOverlay = Polyline()
        routeOverlay.setPoints(routePoints)
        routeOverlay.color = Color.BLUE
        routeOverlay.width = 8.0f
        map.overlays.add(routeOverlay)
        map.invalidate()
    }
}

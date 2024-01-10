package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMyLocationBinding
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MyLocationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyLocationBinding
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var isMoved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyLocation()

        val details = intent.getBooleanExtra("details", false)
        val fromCart = intent.getBooleanExtra("fromCart", false)
        val companyId = intent.getLongExtra("companyId", 0)
        binding.goBack.setOnClickListener {
            if (details) {
                val intent = Intent(this, RegisterFinal::class.java)
                startActivity(intent)
            }
            else if(fromCart) {
                val intent = Intent(this,UserOrderDetails::class.java)
                intent.putExtra("companyId", companyId)
                startActivity(intent)
            }
            else {
                val intent = Intent(this, UserAccountActivity::class.java)
                startActivity(intent)
            }
        }

        binding.saveChangesButton.setOnClickListener {
            if(validInputFields())
                setNewLocation()
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
            val jwtToken = retrieveTokenFromStorage(this)
            if(jwtToken != null) {

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
                        account-> {
                            if((role.equals("Kupac") || role.equals("Dostavljač")))
                                startActivity(Intent(this, UserAccountActivity::class.java))
                            else {
                                val intent   = Intent(this, UserProfileActivity::class.java)
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

                bottomNav.show(account)
            } else {
                val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
                bottomNav.visibility = View.GONE
            }
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
    }

    private fun retrieveTokenFromStorage(context: Context): String? {

        val sharedPreferencesIsUserLoggedIn =
            getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        return sharedPreferencesIsUserLoggedIn.getString("JWT_TOKEN", "token")
    }

    private fun validInputFields(): Boolean {
        if (binding.city.text.isEmpty()) {
            binding.city.error = "Unesite grad."
            return false
        }
        if (binding.postalCode.text.isEmpty()) {
            binding.postalCode.error = "Unesite poštanski broj."
            return false
        }
        if (binding.street.text.isEmpty()) {
            binding.street.error = "Unesite ulicu."
            return false
        }
        if (binding.streetNumber.text.isEmpty()) {
            binding.streetNumber.error = "Unesite broj u ulici."
            return false
        }
        if (!onlyNumbers(binding.streetNumber.text.toString())) {
            binding.streetNumber.error = "Dozvoljeni su samo brojevi."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun setNewLocation() {
        val jsonParams = JSONObject()
        jsonParams.put("city", binding.city.text)
        jsonParams.put("postalCode", binding.postalCode.text)
        jsonParams.put("street", binding.street.text)
        jsonParams.put("streetNumber", binding.streetNumber.text)
        if (isMoved) {
            jsonParams.put("longitude", longitude)
            jsonParams.put("latitude", latitude)
            isMoved = false
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/updateLocation"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->

                if (response.getInt("Message") == 1) {
                    showToastMessage("Promene su sačuvane.", false)

                    getMyLocation()
                }
            },
            { error ->
                showToastMessage("Došlo je do greške, pokušajte ponovo.", true)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                //headers["Authorization"] = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWxldGljbWlsb3MyMDAxQGdtYWlsLmNvbSIsInJvbGVzIjoiUk9MRV9DVVNUT01FUiIsImp0aSI6Ijk4Zjc5NzFjLTU4ZDItNDAzNC1hYjBkLWY4NTFkMWI5OGMxNSIsImV4cCI6MTcwMTM3NjA5MiwiaXNzIjoiLTE5ODY5NzI5MjIifQ.hyYg1-8kkpNAPz8rvZ3Xy8AeWv00yKH9l1wm7fiZQBty7UEpT1lpJlWNPy21ZOGmWG64AHciNlZHzOw9PBJ7Bg"
                headers["Authorization"] = "Bearer " + jwtToken.toString()
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    private fun showPinOnMap() {
        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        mapController = mapView.controller
        mapController.setZoom(8.0)

        mapView.overlays.clear()

        val marker = Marker(mapView)
        marker.position = GeoPoint(latitude, longitude)
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.isDraggable = true
        mapView.overlays.add(marker)
        mapController.setCenter(marker.position)

        marker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
            }

            override fun onMarkerDrag(marker: Marker) {
                val newPosition = marker.position
            }

            override fun onMarkerDragEnd(marker: Marker) {
                val newPosition = marker.position
                latitude = newPosition.latitude
                longitude = newPosition.longitude
                isMoved = true
            }
        })
    }

    private fun getMyLocation() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/getUpdateLocation"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val city: Editable = Editable.Factory.getInstance().newEditable(response.getString("city"))
                binding.city.text = city

                val postalCode: Editable = Editable.Factory.getInstance().newEditable(response.getString("postalCode"))
                binding.postalCode.text = postalCode

                val street: Editable = Editable.Factory.getInstance().newEditable(response.getString("street"))
                binding.street.text = street

                val streetNumber: Editable = Editable.Factory.getInstance().newEditable(response.getString("streetNumber"))
                binding.streetNumber.text = streetNumber

                latitude = response.getString("latitude").toDouble()
                longitude = response.getString("longitude").toDouble()

                showPinOnMap()
            },
            { error ->
//                showToastMessage("Došlo je do greške, pokušajte ponovo.", true)
                latitude = 0.0
                longitude = 0.0
                showPinOnMap()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                //headers["Authorization"] = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWxldGljbWlsb3MyMDAxQGdtYWlsLmNvbSIsInJvbGVzIjoiUk9MRV9DVVNUT01FUiIsImp0aSI6Ijk4Zjc5NzFjLTU4ZDItNDAzNC1hYjBkLWY4NTFkMWI5OGMxNSIsImV4cCI6MTcwMTM3NjA5MiwiaXNzIjoiLTE5ODY5NzI5MjIifQ.hyYg1-8kkpNAPz8rvZ3Xy8AeWv00yKH9l1wm7fiZQBty7UEpT1lpJlWNPy21ZOGmWG64AHciNlZHzOw9PBJ7Bg"
                headers["Authorization"] = "Bearer " + jwtToken.toString()
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@MyLocationActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }
}
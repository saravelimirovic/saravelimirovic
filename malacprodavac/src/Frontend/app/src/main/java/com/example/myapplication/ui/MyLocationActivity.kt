package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityMyLocationBinding
import com.example.myapplication.utils.StaticAddress
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

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var isMoved: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyLocation()

        binding.goBack.setOnClickListener {
            val details = intent.getBooleanExtra("details", false)
            if (details == true) {
                val intent = Intent(this,RegisterFinal::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, UserAccountActivity::class.java)
                startActivity(intent)
            }
        }

        binding.saveChangesButton.setOnClickListener {
            if(validInputFields())
                setNewLocation()
        }
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

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@MyLocationActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }
}
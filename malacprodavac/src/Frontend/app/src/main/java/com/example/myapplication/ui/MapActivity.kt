package com.example.myapplication.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonArrayRequest
import com.example.myapplication.R
import com.example.myapplication.models.CompanyMap
import com.example.myapplication.utils.StaticAddress
import org.json.JSONArray
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import org.json.JSONException

class MapActivity : AppCompatActivity() {
    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val ctx: Context = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        mapView = findViewById(R.id.mapView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        mapController = mapView.controller
        mapController.setZoom(8.0)

        fetchUsersFromDatabase()
    }

    private fun updateUI(users: List<CompanyMap>) {
        for (user in users) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(user.latitude, user.longitude)
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            marker.title = user.companyName
            mapView.overlays.add(marker)
        }

        val centralCoordinates = GeoPoint(44.0165, 21.0059) // Srbija
        mapController.setCenter(centralCoordinates)
    }

    private fun fetchUsersFromDatabase() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/map"
        val jsonObjectRequest = object: JsonArrayRequest(Request.Method.GET, url, null,
            { response ->
                try {
                    val users = parseUserList(response)
                    updateUI(users)
                } catch (e: JSONException) {
                    Log.e("Error", "JSON parsing error: ${e.message}")
                }
            },
            { error ->
                Log.e("Error", "Error: ${error.message}")
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

    private fun parseUserList(response: JSONArray): List<CompanyMap> {
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


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

}
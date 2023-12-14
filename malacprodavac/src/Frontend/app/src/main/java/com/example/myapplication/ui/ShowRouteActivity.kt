package com.example.myapplication.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.utils.getRoute
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class ShowRouteActivity : AppCompatActivity() {

    private lateinit var map: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_route_show)

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))


        val lonStart = intent.getDoubleExtra("lonStart", 0.5)
        val latStart = intent.getDoubleExtra("latStart", 0.5)
        val lonEnd = intent.getDoubleExtra("lonEnd", 0.5)
        val latEnd = intent.getDoubleExtra("latEnd", 0.5)

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

package com.example.myapplication.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DragMapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var draggableMarker: Marker
    private lateinit var coordinatesTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_drag)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, getSharedPreferences("map_prefs", 0))

        mapView = findViewById(R.id.mapView)
        mapView.setBuiltInZoomControls(true)
        mapView.controller.setZoom(15)

        coordinatesTextView = findViewById(R.id.coordinatesTextView)

        draggableMarker = Marker(mapView)
        draggableMarker.position = GeoPoint(44.1738, 43.8519)
        mapView.controller.setCenter(draggableMarker.position)
        draggableMarker.isDraggable = true
        mapView.overlays.add(draggableMarker)

        draggableMarker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {
            }

            override fun onMarkerDrag(marker: Marker) {
                val newPosition = marker.position
                coordinatesTextView.text = "Lat: ${newPosition.latitude}, Lon: ${newPosition.longitude}"
            }

            override fun onMarkerDragEnd(marker: Marker) {
            }
        })
    }
}

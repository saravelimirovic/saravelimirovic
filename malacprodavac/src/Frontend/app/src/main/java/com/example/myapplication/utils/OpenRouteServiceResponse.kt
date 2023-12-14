package com.example.myapplication.utils

import com.google.gson.Gson
import okhttp3.*
import org.osmdroid.util.GeoPoint
import java.io.IOException

data class OpenRouteServiceResponse(
    val features: List<Feature>
)

data class Feature(
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry(
    val coordinates: List<List<Double>>
)

data class Properties(
    val segments: List<Segment>
)

data class Segment(
    val distance: Double
)

// Funkcija za dobijanje rute
fun getRoute(startPoint: GeoPoint, endPoint: GeoPoint, callback: (List<GeoPoint>) -> Unit) {
    val client = OkHttpClient()
    val apiKey = "5b3ce3597851110001cf6248cd45e3c7640d489998f8efb8fc5d3082"

    val url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=$apiKey" +
            "&start=${startPoint.longitude},${startPoint.latitude}" +
            "&end=${endPoint.longitude},${endPoint.latitude}"

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val json = response.body?.string()
                val route = Gson().fromJson(json, OpenRouteServiceResponse::class.java)

                if (route.features.isNotEmpty()) {
                    val coordinates = route.features[0].geometry.coordinates
                    val routePoints = coordinates.map { GeoPoint(it[1], it[0]) }

                    callback(routePoints)
                }
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }
    })
}

// Funkcija za dobijanje udaljenosti
fun getDistance(startPoint: GeoPoint, endPoint: GeoPoint, callback: (Double) -> Unit) {
    val client = OkHttpClient()
    val apiKey = "5b3ce3597851110001cf6248cd45e3c7640d489998f8efb8fc5d3082"

    val url = "https://api.openrouteservice.org/v2/directions/driving-car?api_key=$apiKey" +
            "&start=${startPoint.longitude},${startPoint.latitude}" +
            "&end=${endPoint.longitude},${endPoint.latitude}"

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val json = response.body?.string()
                val route = Gson().fromJson(json, OpenRouteServiceResponse::class.java)

                if (route.features.isNotEmpty()) {
                    val distance = route.features[0].properties.segments.sumByDouble { it.distance }

                    callback(distance)
                }
            }
        }

        override fun onFailure(call: Call, e: IOException) {
            e.printStackTrace()
        }
    })
}

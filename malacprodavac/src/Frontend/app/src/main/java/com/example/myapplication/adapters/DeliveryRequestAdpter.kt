package com.example.myapplication.adapters

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.models.DeliveryRequest
import com.example.myapplication.ui.DeliveryRequests
import com.example.myapplication.ui.UserOrderDetails
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class DeliveryRequestAdpter (context: Context, resource: Int, objects: List<DeliveryRequest>, private val activity: DeliveryRequests):
    ArrayAdapter<DeliveryRequest>(context, resource, objects)  {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                .inflate(R.layout.one_delivery_request, parent, false)
        }

        val request = getItem(position)

        val companyName: TextView = itemView!!.findViewById(R.id.companyName)
        val prosumerName: TextView = itemView!!.findViewById(R.id.prosumerName)
        val startLocation: TextView = itemView!!.findViewById(R.id.startLocation)
        val endLocation: TextView = itemView!!.findViewById(R.id.endLocation)
        val dateAndTime: TextView = itemView!!.findViewById(R.id.dateAndTime)
        val imageViewCompany: ImageView = itemView!!.findViewById(R.id.imageViewCompany)
        val rejectDeliveryRequest: Button = itemView!!.findViewById(R.id.rejectDeliveryRequest)
        val confirmDeliveryRequest: Button = itemView!!.findViewById(R.id.confirmDeliveryRequest)

        // Base64 string koji predstavlja sliku
        val base64Image = "${request?.image}"

        // Dekodiranje Base64 stringa u Bitmap
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        imageViewCompany.setImageBitmap(decodedBitmap)
        companyName.text = request?.companyName
        prosumerName.text = request?.firstNameUser + " " + request?.lastNameUser
        dateAndTime.text = request?.date + " " + request?.time
        startLocation.text = request?.cityStart
        endLocation.text = request?.cityEnd

        rejectDeliveryRequest.setOnClickListener {
            deleteRequest(request?.id, "Odbijeno")
        }

        confirmDeliveryRequest.setOnClickListener {
            deleteRequest(request?.id, "Prihvaceno")
        }

        return itemView
    }

    private fun deleteRequest(id: Long?, message: String) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val url = StaticAddress.URL + "/web/route/deleteDeliveryRequest/$id/$message"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                if (response.getBoolean("Message") == true) {
                    activity.fetchRequests()
                }
            },
            { error ->
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }
}
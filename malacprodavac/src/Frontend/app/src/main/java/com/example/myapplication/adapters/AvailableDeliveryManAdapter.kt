package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.models.AvailableDeliveryMan
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.ui.EnterRouteActivity
import com.example.myapplication.ui.MyCreditCardActivity
import com.example.myapplication.ui.OrdersActivity
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class AvailableDeliveryManAdapter (context: Context, resource: Int, objects: List<AvailableDeliveryMan>) :
    ArrayAdapter<AvailableDeliveryMan>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                .inflate(R.layout.activity_one_available_deliveryman, parent, false)
        }

        val availableDeliveryMan = getItem(position)

        val deliveryManName: TextView = itemView!!.findViewById(R.id.deliveryman_name)
        val price: TextView = itemView!!.findViewById(R.id.delivery_price)
        val deliveryManDate: TextView = itemView!!.findViewById(R.id.deliveryman_date)
        val startingRoute: TextView = itemView!!.findViewById(R.id.starting_route)
        val finishRoute: TextView = itemView!!.findViewById(R.id.finish_route)
        val imageView: ImageView = itemView!!.findViewById(R.id.deliveryman_image)
        val button: Button = itemView!!.findViewById(R.id.sendDeliveryRequest)

        // Base64 string koji predstavlja sliku
        val base64Image = "${availableDeliveryMan?.userImage}"

        // Dekodiranje Base64 stringa u Bitmap
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        imageView.setImageBitmap(decodedBitmap)
        deliveryManName.text = availableDeliveryMan?.firstNameUser + " " + availableDeliveryMan?.lastNameUser
        price.text = "50.99"
        deliveryManDate.text = availableDeliveryMan?.date + " " + availableDeliveryMan?.time
        startingRoute.text = availableDeliveryMan?.from
        finishRoute.text = availableDeliveryMan?.to

        button.setOnClickListener {
//            Toast.makeText(context, "${availableDeliveryMan?.date}", Toast.LENGTH_LONG).show()
            addRequest(availableDeliveryMan?.from, availableDeliveryMan?.to, availableDeliveryMan?.date, availableDeliveryMan?.time, availableDeliveryMan?.userId)
        }

        return itemView
    }

    private fun addRequest(cityStart: String?, cityEnd: String?, date: String?, time: String?, delivererId: Long?) {
        val jsonParams = JSONObject()
        jsonParams.put("cityStart", cityStart)
        jsonParams.put("cityEnd", cityEnd)
        jsonParams.put("date", date)
        jsonParams.put("time", time)
        jsonParams.put("delivererId", delivererId)

        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val url = StaticAddress.URL + "/web/route/addRequest"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                Toast.makeText(context, "Zahtev je poslat.", Toast.LENGTH_LONG).show()
                val intent   = Intent(context, OrdersActivity::class.java)
                context.startActivity(intent)
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

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }
}
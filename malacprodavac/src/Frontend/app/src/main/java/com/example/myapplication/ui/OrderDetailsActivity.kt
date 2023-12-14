package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.adapters.OrderDetailsOneOrderAdapter
import com.example.myapplication.adapters.OrdersAdapter
import com.example.myapplication.databinding.ActivityOneCreditCardBinding
import com.example.myapplication.databinding.ActivityOrderDetailsBinding
import com.example.myapplication.models.Order
import com.example.myapplication.models.OrderDetailsOneOrder
import com.example.myapplication.utils.StaticAddress
import java.time.LocalDate
import java.util.HashMap

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton : Button = findViewById(R.id.go_back)
        backButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        val orderId = intent.getLongExtra("orderId", 0)
        fetchProducList(orderId)
        fetchCustomer(orderId)
    }

    private fun fetchProducList(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/order/productsInOrder/$id"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val orderList: MutableList<OrderDetailsOneOrder> = ArrayList()
                var totalPrice = 0.0
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    orderList.add(OrderDetailsOneOrder(objekat.getLong("productId"),
                        objekat.getString("name"),
                        objekat.getDouble("quantity").toInt(),
                        objekat.getString("measuringUnit"),
                        objekat.getDouble("price")))
                    totalPrice += objekat.getDouble("price")
                }
                binding.totalPrice.text = totalPrice.toString() + " RSD"

                val orders = orderList
                val adapter = OrderDetailsOneOrderAdapter(this, R.layout.activity_one_ordered_item, orders)
                val listView: ListView = findViewById(R.id.orderItemsListView)
                listView.adapter = adapter
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

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)

    }

    private fun fetchCustomer(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/order/userInOrder/$id"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val fullName: Editable = Editable.Factory.getInstance().newEditable(response.getString("fullName"))
                binding.name.text = fullName
                binding.kupac.text = fullName
                val cityName: Editable = Editable.Factory.getInstance().newEditable(response.getString("cityName"))
                binding.location.text = cityName
                val streetName: Editable = Editable.Factory.getInstance().newEditable(response.getString("streetName"))
                binding.street.text = streetName
                val phoneNumber: Editable = Editable.Factory.getInstance().newEditable(response.getString("phoneNumber"))
                binding.phone.text = phoneNumber
                val delivery: Editable = Editable.Factory.getInstance().newEditable(response.getBoolean("delivery").toString())
                if (delivery.toString() == "true")
                    binding.delivery.isChecked = true
                else
                    binding.delivery.isChecked = false
//                val paymentMethod: Editable = Editable.Factory.getInstance().newEditable(response.getString("paymentMethod"))
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
}
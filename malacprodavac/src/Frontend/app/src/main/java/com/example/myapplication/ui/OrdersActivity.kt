package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.adapters.CreditCardAdapter
import com.example.myapplication.adapters.OrdersAdapter
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.models.Order
import com.example.myapplication.utils.StaticAddress
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class OrdersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        val backButton : Button = findViewById(R.id.go_back)
        backButton.setOnClickListener {
            val intent = Intent(this, AddPayementMethod::class.java)
            startActivity(intent)
        }

        fetchOrderData()
    }

    private fun fetchOrderData() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/order/listOfOrders"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val orderList: MutableList<Order> = ArrayList()
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    orderList.add(Order(objekat.getLong("orderId"),
                                        objekat.getString("fullNameCustomer"),
                                        objekat.getString("deliveryCity"),
                                        objekat.getString("dateOrderIsMade"),
                                        objekat.getDouble("totalPrice")))
                }

                val orders = orderList
                val adapter = OrdersAdapter(this, R.layout.activity_one_order, orders)
                val listView: ListView = findViewById(R.id.ordersListView)
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

}
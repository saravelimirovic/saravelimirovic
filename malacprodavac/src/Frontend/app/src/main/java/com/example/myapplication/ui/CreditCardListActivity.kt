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
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.adapters.CreditCardAdapter
import com.example.myapplication.adapters.OrderDetailsOneOrderAdapter
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.models.OrderDetailsOneOrder
import com.example.myapplication.utils.StaticAddress
import org.json.JSONArray

class CreditCardListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_payment_method)

        val addCreditCard: Button = findViewById(R.id.add_credit_card)
        addCreditCard.setOnClickListener {
            val intent = Intent(this, AddNewCreditCardActivity::class.java)
            startActivity(intent)
        }

        val goBack: Button = findViewById(R.id.go_back)
        goBack.setOnClickListener {
            finish()
        }

        val addCard: Button = findViewById(R.id.add_credit_card)
        addCard.setOnClickListener {
            val intent = Intent(this, AddNewCreditCardActivity::class.java)
            startActivity(intent)
        }

        fetchCardsData()
    }

    private fun fetchCardsData() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/card/allCards"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val cardList: MutableList<CreditCardListItem> = ArrayList()
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    cardList.add(CreditCardListItem(objekat.getLong("id"), objekat.getString("nameOnCard")))
                }

                val cards = cardList
                val adapter = CreditCardAdapter(this, R.layout.activity_credit_card_list, cards)
                val listView: ListView = findViewById(R.id.creditCardListView)
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
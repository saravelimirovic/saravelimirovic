package com.example.myapplication.ui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.example.myapplication.databinding.ActivityOneCreditCardBinding
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.utils.StaticAddress

class MyCreditCardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOneCreditCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOneCreditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getLongExtra("id", 0)
        getCard(id)

        binding.deleteCreditCard.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            deleteCard(id)
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste sigurni da želite da izbrišete karticu?")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

        binding.goBack.setOnClickListener {
            val details = intent.getBooleanExtra("details", false)
            if (details == true) {
                val intent = Intent(this,RegisterFinal::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, CreditCardListActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getCard(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/card/$id"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val nameOnCard: Editable = Editable.Factory.getInstance().newEditable(response.getString("nameOnCard"))
                binding.cardName.text = nameOnCard

                val cardNumber: Editable = Editable.Factory.getInstance().newEditable(response.getString("cardNumber"))
                binding.cardNumber.text = cardNumber

                val cardExpiration: Editable = Editable.Factory.getInstance().newEditable(response.getString("cardExpiration"))
                binding.expirationDate.text = cardExpiration

                val securityCode: Editable = Editable.Factory.getInstance().newEditable(response.getString("securityCode"))
                binding.securityCode.text = securityCode
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

    private fun deleteCard(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/card/deleteCard/$id"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                Toast.makeText(this, "Kartica je uspešno obrisana.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, CreditCardListActivity::class.java)
                startActivity(intent)
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
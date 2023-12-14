package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.databinding.ActivityAddNewCreditCardBinding
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class AddNewCreditCardActivity: AppCompatActivity() {

    private lateinit var binding: ActivityAddNewCreditCardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNewCreditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.addCardButton.setOnClickListener {
            if(validInputFields())
                addCreditCard()
        }
    }

    private fun validInputFields(): Boolean {
        if (binding.cardName.text.isEmpty()) {
            binding.cardName.error = "Unesite naziv kartice."
            return false
        }
        if (binding.cardNumber.text.isEmpty()) {
            binding.cardNumber.error = "Unesite broj karticie."
            return false
        }
        val regex = Regex("^(\\d{4}-){3}\\d{4}$")
        if(!regex.matches(binding.cardNumber.text.toString())) {
            binding.cardNumber.error = "Ovo polje zahteva 16 cifara i nakon svake četvrte cifre karakter '-'."
            return false
        }
        if (binding.expirationDate.text.isEmpty()) {
            binding.expirationDate.error = "Unesite datum isteka kartice."
            return false
        }
        val regexExpirationDate = Regex("^(0[1-9]|1[0-2])-(20[2-9][0-9])$")
        if(!regexExpirationDate.matches(binding.expirationDate.text.toString())) {
            binding.expirationDate.error = "Ovo polje zahteva format MM-YYYY."
            return false
        }
        if (binding.securityCode.text.isEmpty()) {
            binding.securityCode.error = "Unesite sigurnosni kod kartice."
            return false
        }
        if (binding.securityCode.text.length != 3 || !onlyNumbers(binding.securityCode.text.toString())) {
            binding.securityCode.error = "Ovo polje zahteva 3 cifre."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun addCreditCard() {
        val jsonParams = JSONObject()
        jsonParams.put("nameOnCard", binding.cardName.text)
        jsonParams.put("cardNumber", binding.cardNumber.text)
        jsonParams.put("cardExpiration", "01-" + binding.expirationDate.text)
        jsonParams.put("securityCode", binding.securityCode.text)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/card/add"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->

                if (response.getInt("Message") == 1) {
                    showToastMessage("Kartica uspešno dodata.", false)
                }
            },
            { error ->
                showToastMessage("Došlo je do greške, pokušajte ponovo.", true)
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

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@AddNewCreditCardActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }
}
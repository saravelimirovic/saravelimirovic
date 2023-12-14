package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject

class EnterRouteActivity : AppCompatActivity(){

    private lateinit var goBack: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_route)

        val buttonShowRoute: Button = findViewById(R.id.buttonShowRoute)
        buttonShowRoute.setOnClickListener {
            if(isInputValid()) {
                val cityStart = findViewById<EditText>(R.id.editTextStart).text.toString().trim()
                val cityEnd = findViewById<EditText>(R.id.editTextEnd).text.toString().trim()
                val postalCodeStart = findViewById<EditText>(R.id.postalCodeStart).text.toString().trim()
                val postalCodeEnd = findViewById<EditText>(R.id.postalCodeEnd).text.toString().trim()

                //Toast.makeText(this, cityStart + " : " + postalCodeStart + " : " + cityEnd + " : " + postalCodeEnd, Toast.LENGTH_LONG).show()
                //sendLocationInfo(cityStart, postalCodeStart, cityEnd, postalCodeEnd)

                val intent = Intent(this, AddNewRouteDateAndTimeActivity::class.java)
                intent.putExtra("cityStart", cityStart)
                intent.putExtra("cityEnd", cityEnd)
                intent.putExtra("postalCodeStart", postalCodeStart)
                intent.putExtra("postalCodeEnd", postalCodeEnd)
                startActivity(intent)
            }
        }

        goBack = findViewById(R.id.go_back)
        goBack.setOnClickListener {
            val intent = Intent(this, AddPayementMethod::class.java)
            startActivity(intent)
        }

    }

    private fun sendLocationInfo(cityStart: String, postalCodeStart: String, cityEnd: String, postalCodeEnd: String) {
        val jsonParams = JSONObject()
        jsonParams.put("cityStart", cityStart)
        jsonParams.put("postalCodeStart", postalCodeStart)
        jsonParams.put("cityEnd", cityEnd)
        jsonParams.put("postalCodeEnd", postalCodeEnd)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/route"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                val responseBody = response.toString()
                val jsonResponse = JSONObject(responseBody)

                val intent = Intent(this, ShowRouteActivity::class.java)
                intent.putExtra("lonStart", jsonResponse.getDouble("lonStart"))
                intent.putExtra("latStart", jsonResponse.getDouble("latStart"))
                intent.putExtra("lonEnd", jsonResponse.getDouble("lonEnd"))
                intent.putExtra("latEnd", jsonResponse.getDouble("latEnd"))
                startActivity(intent)
            },
            { error ->

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

    private fun isInputValid(): Boolean {
        val cityStartEditText = findViewById<EditText>(R.id.editTextStart)
        val cityEndEditText = findViewById<EditText>(R.id.editTextEnd)
        val postalCodeStartEditText = findViewById<EditText>(R.id.postalCodeStart)
        val postalCodeEndEditText = findViewById<EditText>(R.id.postalCodeEnd)

        val cityStart = findViewById<EditText>(R.id.editTextStart).text.toString().trim()
        val cityEnd = findViewById<EditText>(R.id.editTextEnd).text.toString().trim()
        val postalCodeStart = findViewById<EditText>(R.id.postalCodeStart).text.toString().trim()
        val postalCodeEnd = findViewById<EditText>(R.id.postalCodeEnd).text.toString().trim()

        if(cityStart.isEmpty()) {
            cityStartEditText.error = "Unesite naziv grada."
            return false
        }
        if(postalCodeStart.isEmpty()) {
            postalCodeStartEditText.error = "Unesite poštanski broj."
            return false
        }
        if(!onlyNumbers(postalCodeStart) || postalCodeStart.length != 5) {
            postalCodeStartEditText.error = "Unesite 5 cifara."
            return false
        }
        if(cityEnd.isEmpty()) {
            cityEndEditText.error = "Unesite naziv grada."
            return false
        }
        if(postalCodeEnd.isEmpty()) {
            postalCodeEndEditText.error = "Unesite poštanski broj."
            return false
        }
        if(!onlyNumbers(postalCodeEnd) || postalCodeEnd.length != 5) {
            postalCodeEndEditText.error = "Unesite 5 cifara."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }
}
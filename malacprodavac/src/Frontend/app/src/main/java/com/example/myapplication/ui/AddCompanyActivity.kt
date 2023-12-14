package com.example.myapplication.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.utils.StaticAddress
import org.json.JSONObject
import java.util.HashMap

class AddCompanyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_company)

        val addCompanyButton: Button = findViewById(R.id.add_company_button)
        val backButton: Button = findViewById(R.id.backButton)
        val name: EditText = findViewById<EditText?>(R.id.company_name)
        val yearOfCreation: EditText = findViewById<EditText?>(R.id.company_year_of_creation)
        val cityName: EditText = findViewById<EditText?>(R.id.company_city)
        val streetName: EditText = findViewById<EditText?>(R.id.company_street)
        val streetNumber: EditText = findViewById<EditText?>(R.id.company_street_number)

        backButton.setOnClickListener {
            val details = intent.getBooleanExtra("details", false)
            if (details == true) {
                val intent = Intent(this, RegisterFinal::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, HomeActivity::class.java) //kad se doda strana na kojoj je dugme za dodvanje kompanije
                startActivity(intent)
            }
        }

        addCompanyButton.setOnClickListener {
            if(checkFields())
                addCompany(name.text.toString(), yearOfCreation.text.toString(), cityName.text.toString(), streetName.text.toString(), streetNumber.text.toString())
        }
    }

    private fun checkFields() : Boolean {
        val companyNameEditText = findViewById<EditText>(R.id.company_name)
        val yearEditText = findViewById<EditText>(R.id.company_year_of_creation)
        val cityEditText = findViewById<EditText>(R.id.company_city)
        val postalCodeEditText = findViewById<EditText>(R.id.company_postal_code)
        val streetEditText = findViewById<EditText>(R.id.company_street)
        val streetNumberEditText = findViewById<EditText>(R.id.company_street_number)

        val name = companyNameEditText.text.toString().trim()
        val year = yearEditText.text.toString().trim()
        val city = cityEditText.text.toString().trim()
        val postalCode = postalCodeEditText.text.toString().trim()
        val street = streetEditText.text.toString().trim()
        val streetNumber = streetNumberEditText.text.toString().trim()

        if (name.isEmpty()) {
            companyNameEditText.error = "Unesite naziv kompanije."
            return false
        }

        if (year.isEmpty()) {
            yearEditText.error = "Unesite godinu osnivanja."
            return false
        }

        if (!onlyNumbers(year)) {
            yearEditText.error = "Dozvoljeni su samo brojevi."
            return false
        }

        if (city.isEmpty()) {
            cityEditText.error = "Unesite grad u kome se kompanija nalazi."
            return false
        }

        if(postalCode.isEmpty()) {
            postalCodeEditText.error = "Unesite poštanski broj grada."
            return false
        }

        if (!onlyNumbers(postalCode)) {
            postalCodeEditText.error = "Dozvoljeni su samo brojevi."
            return false
        }

        if(street.isEmpty()) {
            streetEditText.error = "Unesite ulicu."
            return false
        }

        if(streetNumber.isEmpty()) {
            streetNumberEditText.error = "Unesite broj u ulici."
            return false
        }

        if (!onlyNumbers(streetNumber)) {
            streetNumberEditText.error = "Dozvoljeni su samo brojevi."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun addCompany(name: String, yearOfCreation: String, cityName: String, streetName: String, streetNumber: String) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/add"

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("yearOfCreation", yearOfCreation.toInt())
        jsonParams.put("cityName", cityName)
        jsonParams.put("streetName", streetName)
        jsonParams.put("streetNumber", streetNumber)

        val JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                Toast.makeText(this, "Kompanija kreirana", Toast.LENGTH_LONG).show()
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

        JsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(JsonObjectRequest)
    }
}
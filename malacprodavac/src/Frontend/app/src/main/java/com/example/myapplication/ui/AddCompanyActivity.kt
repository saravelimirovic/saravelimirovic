package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.utils.StaticAddress
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject
import java.io.IOException
import java.util.HashMap

class AddCompanyActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private var image: String = ""

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
        val postalCode: EditText = findViewById<EditText?>(R.id.company_postal_code)

        backButton.setOnClickListener {
            val details = intent.getBooleanExtra("details", false)
            if (details == true) {
                val intent = Intent(this, RegisterFinal::class.java)
                startActivity(intent)
            }
        }

        addCompanyButton.setOnClickListener {
            if(checkFields())
                addCompany(name.text.toString(), yearOfCreation.text.toString(), cityName.text.toString(), streetName.text.toString(), streetNumber.text.toString(), postalCode.text.toString(), image)
        }

        val button = findViewById<AppCompatImageButton>(R.id.addCompanyImageButton)
        button.setOnClickListener {
            launcher.launch(
                ImagePicker.with(this)
                    .crop()
                    .galleryOnly()//Crop image(Optional), Check Customization for more option
                    .setMultipleAllowed(false)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent()
            )
        }

    }


    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                imageView = findViewById(R.id.AddedImageView2)
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(uri)

                uri?.let { selectedUri ->
                    try {
                        val inputStream = contentResolver.openInputStream(selectedUri)
                        val imageBytes = inputStream?.readBytes()

                        if (imageBytes != null) {
                            // Kodiranje bajtova u Base64
                            image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                            // Toast.makeText(this, image, Toast.LENGTH_LONG).show()
                        } else {
                            // Greška pri čitanju bajtova
                            Toast.makeText(this, "Greška pri čitanju slike", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                        // Dodajte dodatnu obradu greške prema potrebi
                    }
                }
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

    private fun addCompany(name: String, yearOfCreation: String, cityName: String, streetName: String, streetNumber: String, postalCode: String, image: String) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/add"

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("yearOfCreation", yearOfCreation.toInt())
        jsonParams.put("cityName", cityName)
        jsonParams.put("streetName", streetName)
        jsonParams.put("streetNumber", streetNumber)
        jsonParams.put("postalCode", postalCode)
        jsonParams.put("logo", image)

        val JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                if (response.getInt("Message") == 1) {
                    Toast.makeText(this, "Kompanija kreirana", Toast.LENGTH_LONG).show()
                    val newIntent = Intent(this, RegisterFinal::class.java)
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putBoolean("companyAdded", true)
                    editor.apply()
                    startActivity(newIntent)
                }

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

        JsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(JsonObjectRequest)
    }
}
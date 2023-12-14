

package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.utils.StaticAddress
import com.github.drjacky.imagepicker.ImagePicker
import org.json.JSONObject

class AddProductActivity : AppCompatActivity() {
    private lateinit var imageView:ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        imageView = findViewById(R.id.AddedImageView)

        val button = findViewById<AppCompatImageButton>(R.id.add_images_button)
        button.setOnClickListener {

            launcher.launch(
                ImagePicker.with(this)
                    .crop()
                    .galleryOnly()//Crop image(Optional), Check Customization for more option
                    .setMultipleAllowed(true)
                    .maxResultSize(
                        1080,
                        1080
                    )    //Final image resolution will be less than 1080 x 1080(Optional)
                    .createIntent()
            )
        }

        val backButton = findViewById<Button>(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        val spinnerMeasuringUnit = arrayOf<String>("kilogram", "litar")
        val spinnerCategory = arrayOf<String>("freshFruits", "driedFruits", "vegetables",
                                            "meatProducts", "dairyProducts", "organicProducts",
                                            "spices", "herbs", "flowersAndSeedlings", "beeProducts",
                                            "cannedFoods", "handMadeCrafts")
        var measuringUnit = ""
        var category = ""


        val spinner2: Spinner = findViewById(R.id.product_measuring_unit)
        spinner2?.adapter = ArrayAdapter(this,  androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerMeasuringUnit)
        spinner2?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                print("Nothing selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                //Toast.makeText(activity, type, Toast.LENGTH_LONG).show()
                measuringUnit = type
            }
        }

        val spinner3: Spinner = findViewById(R.id.product_category)
        spinner3?.adapter = ArrayAdapter(this,  androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, spinnerCategory)
        spinner3?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                print("Nothing selected")
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val type = parent?.getItemAtPosition(position).toString()
                //Toast.makeText(activity, type, Toast.LENGTH_LONG).show()
                category = type
            }
        }

        val productName: EditText = findViewById(R.id.product_name)
        val productPrice: EditText = findViewById(R.id.product_price)
        val description: EditText = findViewById(R.id.description)
        val addButton: Button = findViewById(R.id.add_product_button)

        addButton.setOnClickListener {
            if(checkFields()) {
                addProduct(productName.text.toString(), productPrice.text.toString(), description.text.toString(), measuringUnit, category)
                sendNotificationFunction()
            }
        }
    }

    private fun checkFields() : Boolean {
        val productNameEditText = findViewById<EditText>(R.id.product_name)
        val priceEditText = findViewById<EditText>(R.id.product_price)
        val descriptionEditText = findViewById<EditText>(R.id.description)

        val name = productNameEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()

        if (name.isEmpty()) {
            productNameEditText.error = "Unesite naziv proizvoda."
            return false
        }

        if (price.isEmpty()) {
            priceEditText.error = "Unesite cenu proizvoda."
            return false
        }

        if (!priceFormat(price)) {
            priceEditText.error = "Dozvoljeni su samo brojevi ili decimalni brojevi."
            return false
        }

        if (description.isEmpty()) {
            descriptionEditText.error = "Unesite opis proizvoda."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun priceFormat(string: String): Boolean {
        return string.matches(Regex("^\\d+(\\.\\d+)?\$"))
    }

    private fun addProduct(productName: String, productPrice: String, description: String, measuringUnit: String, category: String) {
        val jsonParams = JSONObject()
        jsonParams.put("name",productName)
        jsonParams.put("description", description)
        jsonParams.put("measuringUnit", measuringUnit)
        jsonParams.put("productCategory", category)
        jsonParams.put("quantity", 10)
        jsonParams.put("price", productPrice)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/product/add"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                Toast.makeText(this, "Product added", Toast.LENGTH_LONG).show();
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

    private fun sendNotificationFunction() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/notification/add-product"

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "empty")
        val lastName = sharedPreferences.getString("lastName", "empty")
        val email = sharedPreferences.getString("email", "empty")

        val jsonParams = JSONObject()
        jsonParams.put("title", "Nov proizvod")
        jsonParams.put("body", "$firstName $lastName je izbacio/la nov proizvod.")
        jsonParams.put("email", email)
        jsonParams.put("image", "https://i.postimg.cc/ZKBqMLDX/ic-launcher-playstore.png")

        val JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->

            },
            { error ->

            }) {
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                val headers = HashMap<String, String>()
//
//                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")
//
//                headers["Authorization"] = "Bearer $jwtToken"
//                return headers
//            }
        }

        JsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(JsonObjectRequest)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {

                // Use the uri to load the image
                // Only if you are not using crop feature:

                //////////////
            }
        }
}


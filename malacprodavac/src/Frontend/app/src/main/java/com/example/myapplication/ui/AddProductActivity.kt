package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
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

class AddProductActivity : AppCompatActivity() {
    private lateinit var imageView:ImageView
    private var image: String = ""

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var toggle: ActionBarDrawerToggle

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        imageView = findViewById(R.id.AddedImageView)

        val button = findViewById<AppCompatImageButton>(R.id.addProductImageButton)
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

        val spinnerMeasuringUnit = arrayOf<String>("kilogram", "litar")
        val spinnerCategory = arrayOf<String>("Sveže voće", "Sušeno voće", "Povrće",
            "Meso", "Mlečni proizvodi", "Organski proizvodi",
            "Začini", "Sveže bilje", "Semenke", "Pčelinji proizvodi",
            "Konzervirana hrana", "Ručni rad")
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
        val quantity: EditText = findViewById(R.id.quantity)
        val addButton: Button = findViewById(R.id.add_product_button)

        addButton.setOnClickListener {
            if(checkFields()) {
                val kategorija = setCategory(category)
                addProduct(productName.text.toString(), productPrice.text.toString(), description.text.toString(), measuringUnit, kategorija, image, quantity.text.toString().toDouble())
                sendNotificationFunction()
            }
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById<NavigationView>(R.id.drawer_view)

        val menu_button = findViewById<ImageButton>(R.id.menu)

        menu_button.setOnClickListener{
            if(!drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.openDrawer(GravityCompat.START)
            else
                drawerLayout.closeDrawer(GravityCompat.END)
        }


        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "empty")

        if (role == "Dostavljač") {
            navView.menu.removeItem(R.id.kupovina1)
            navView.menu.removeItem(R.id.nalog1)
        }

        if (role == "Prodavac") {
            navView.menu.removeItem(R.id.kupovina2)
            navView.menu.removeItem(R.id.nalog2)
        }
        if (role == "Kupac") {
            navView.menu.removeItem(R.id.kupovina2)
            navView.menu.removeItem(R.id.kupovina1)
            navView.menu.removeItem(R.id.nalog1)
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId){

                R.id.drawer_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                }

                R.id.drawer_explore -> {
                    startActivity(Intent(this, ExploreActivity::class.java))
                    finish()
                }

                R.id.drawer_notf -> {
                    startActivity(Intent(this, NotificationsActivity::class.java))
                    finish()
                }

                R.id.drawer_profile -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                    finish()
                }

                 R.id.drawer_account1 -> {
                    startActivity(Intent(this, UserAccountActivity::class.java))
                    finish()
                }

                R.id.drawer_account2 -> {
                    startActivity(Intent(this, UserAccountActivity::class.java))
                    finish()
                }

                R.id.drawer_logout -> {
                    val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    editor.remove("JWT_TOKEN")
                    editor.remove("firstName")
                    editor.remove("lastName")
                    editor.remove("email")
                    editor.remove("password")
                    editor.remove("companyAdded")
                    editor.apply()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

            }
            true
        }

        try {
            @SuppressLint("MissingInflatedId", "LocalSuppress")
            val bottomNav = findViewById<MeowBottomNavigation>(R.id.meowBottomNav)
            bottomNav.add(MeowBottomNavigation.Model(home,R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search,R.drawable.search_black_24dp__1_))
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val role = sharedPreferences.getString("role", "empty")

            if (role == "Dostavljač" || role == "Prodavac") {
                bottomNav.add(MeowBottomNavigation.Model(add, R.drawable.round_add_24))
            }
            bottomNav.add(MeowBottomNavigation.Model(notf,R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account,R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    account -> {
                        if (role == "Prodavac") {
                            startActivity(Intent(this, UserProfileActivity::class.java))
                        } else {
                            val intent = Intent(this, UserAccountActivity::class.java)
                            intent.putExtra("MyProfile", true)
                            startActivity(intent)
                        }
                    }

                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            bottomNav.show(add)

        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }

    }

    private fun checkFields() : Boolean {
        val productNameEditText = findViewById<EditText>(R.id.product_name)
        val priceEditText = findViewById<EditText>(R.id.product_price)
        val descriptionEditText = findViewById<EditText>(R.id.description)
        val quantityEditText = findViewById<EditText>(R.id.quantity)

        val name = productNameEditText.text.toString().trim()
        val price = priceEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim()
        val quantity = quantityEditText.text.toString().trim()

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

        if (quantity.isEmpty()) {
            descriptionEditText.error = "Unesite količinu proizvoda."
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

    private fun addProduct(productName: String, productPrice: String, description: String, measuringUnit: String, category: String, image: String, quantity: Double) {
        val jsonParams = JSONObject()
        jsonParams.put("name",productName)
        jsonParams.put("description", description)
        jsonParams.put("measuringUnit", measuringUnit)
        jsonParams.put("productCategory", category)
        jsonParams.put("quantity", quantity)
        jsonParams.put("price", productPrice)
        jsonParams.put("image", image)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/product/add"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                Toast.makeText(this, "Proizvod uspešno dodat.", Toast.LENGTH_LONG).show();
                val intent = Intent(this, AddProductActivity::class.java)
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
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                imageView = findViewById(R.id.AddedImageView)
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



    private fun uriToByteArray(uri: Uri): ByteArray? {
        val inputStream = contentResolver.openInputStream(uri)
        return inputStream?.readBytes()
    }

    private fun setCategory(category: String): String {
        if(category.equals("Sveže voće"))
            return "freshFruits"
        else if(category.equals("Sušeno voće"))
            return "driedFruits"
        else if(category.equals("Povrće"))
            return "vegetables"
        else if(category.equals("Meso"))
            return "meatProducts"
        else if(category.equals("Mlečni proizvodi"))
            return "dairyProducts"
        else if(category.equals("Organski proizvodi"))
            return "organicProducts"
        else if(category.equals("Začini"))
            return "spices"
        else if(category.equals("Sveže bilje"))
            return "herbs"
        else if(category.equals("Semenke"))
            return "flowersAndSeedlings"
        else if(category.equals("Pčelinji proizvodi"))
            return "beeProducts"
        else if(category.equals("Konzervirana hrana"))
            return "cannedFoods"
        else
            return "handMadeCrafts"
    }
}
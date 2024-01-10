package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Base64
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.myapplication.databinding.ActivityMyInfoBinding
import com.example.myapplication.databinding.ActivityUserAccountBinding
import com.example.myapplication.utils.StaticAddress
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject
import java.io.IOException

class MyInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyInfoBinding
    private lateinit var imageView: ImageView
    private var image: String = ""
    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getMyInfo()

        binding.goBack.setOnClickListener {
            val intent = Intent(this, UserAccountActivity::class.java)
            startActivity(intent)
        }

        binding.saveChangesButton.setOnClickListener {
            if(validInputFields())
                setNewInfo()
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById<NavigationView>(R.id.drawer_view)

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
            bottomNav.add(MeowBottomNavigation.Model(home, R.drawable.home_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(search, R.drawable.search_black_24dp__1_))
            val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val role = sharedPreferences.getString("role", "empty")

            if (role == "Dostavljač" || role == "Prodavac") {
                bottomNav.add(MeowBottomNavigation.Model(add, R.drawable.round_add_24))
            }
            bottomNav.add(MeowBottomNavigation.Model(notf, R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account, R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {

                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
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
                    add -> {
                        if (role == "Dostavljač") {
                            val intent = Intent(this, EnterRouteActivity::class.java)
                            startActivity(intent)
                        }
                        if (role != "Dostavljač" && role != "Kupac") {
                            val intent = Intent(this, AddProductActivity::class.java)
                            startActivity(intent)
                        }
                    }
                }

            }
            bottomNav.show(account)

        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }

        binding.addUserImageButton.setOnClickListener {
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
                imageView = findViewById(R.id.imageId)
                imageView.visibility = View.VISIBLE
                imageView.setImageURI(uri)

                uri?.let { selectedUri ->
                    try {
                        val inputStream = contentResolver.openInputStream(selectedUri)
                        val imageBytes = inputStream?.readBytes()

                        if (imageBytes != null) {
                            // Kodiranje bajtova u Base64
                            image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
//                            Toast.makeText(this, image, Toast.LENGTH_LONG).show()
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

    private fun validInputFields(): Boolean {
        if (binding.firstName.text.isEmpty()) {
            binding.firstName.error = "Unesite ime."
            return false
        }
        if (binding.lastName.text.isEmpty()) {
            binding.lastName.error = "Unesite prezime."
            return false
        }
        if (binding.phoneNumber.text.isEmpty()) {
            binding.phoneNumber.error = "Unesite broj telefona."
            return false
        }

        if (!onlyNumbers(binding.phoneNumber.text.toString())) {
            binding.phoneNumber.error = "Dozvoljeni su samo brojevi."
            return false
        }

        return true
    }

    private fun onlyNumbers(string: String): Boolean {
        return string.contains(Regex("^\\d+\$"))
    }

    private fun getMyInfo() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/getUpdateInfo"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val firstName: Editable = Editable.Factory.getInstance().newEditable(response.getString("firstName"))
                binding.firstName.text = firstName

                val lastName: Editable = Editable.Factory.getInstance().newEditable(response.getString("lastName"))
                binding.lastName.text = lastName

                val phoneNumber: Editable = Editable.Factory.getInstance().newEditable(response.getString("phoneNumber"))
                binding.phoneNumber.text = phoneNumber

                // Base64 string koji predstavlja sliku
                val base64Image = response.getString("image")

                // Dekodiranje Base64 stringa u Bitmap
                val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
                val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                binding.imageId.setImageBitmap(decodedBitmap)
                binding.imageId.visibility = View.VISIBLE
            },
            { error ->
                showToastMessage("Došlo je do greške, pokušajte ponovo.", true)
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                //headers["Authorization"] = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJtaWxldGljbWlsb3MyMDAxQGdtYWlsLmNvbSIsInJvbGVzIjoiUk9MRV9DVVNUT01FUiIsImp0aSI6Ijk4Zjc5NzFjLTU4ZDItNDAzNC1hYjBkLWY4NTFkMWI5OGMxNSIsImV4cCI6MTcwMTM3NjA5MiwiaXNzIjoiLTE5ODY5NzI5MjIifQ.hyYg1-8kkpNAPz8rvZ3Xy8AeWv00yKH9l1wm7fiZQBty7UEpT1lpJlWNPy21ZOGmWG64AHciNlZHzOw9PBJ7Bg"
                headers["Authorization"] = "Bearer " + jwtToken.toString()
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    private fun setNewInfo() {
        val jsonParams = JSONObject()
        jsonParams.put("firstName", binding.firstName.text)
        jsonParams.put("lastName", binding.lastName.text)
        jsonParams.put("phoneNumber", binding.phoneNumber.text)
        if (!image.equals(""))
            jsonParams.put("image", image)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/updateInfo"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->

                if (response.getInt("Message") == 1) {
                    showToastMessage("Promene su sačuvane.", false)
                }

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("firstName", binding.firstName.text.toString())
                editor.putString("lastName", binding.lastName.text.toString())
                editor.apply()
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
        val toast = Toast.makeText(this@MyInfoActivity, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) Color.RED else Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.WHITE)
        toast.show()
    }

}
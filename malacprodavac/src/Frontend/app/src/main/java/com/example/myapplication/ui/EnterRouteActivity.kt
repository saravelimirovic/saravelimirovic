package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.material.navigation.NavigationView
import org.json.JSONObject

class EnterRouteActivity : AppCompatActivity(){


    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var toggle: ActionBarDrawerToggle
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
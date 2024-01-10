package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject
import java.util.HashMap

class AddPayementMethod : AppCompatActivity() {
    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_payement_method)

        setVisibility()

        val profileButton: Button = findViewById(R.id.profileButton)
        val productButton: Button = findViewById(R.id.productButton)
        val mapButton: Button = findViewById(R.id.mapButton)
        val accountButton: Button = findViewById(R.id.accountButton)
        val producerButton: Button = findViewById(R.id.producerButton)
        val addRouteButton: Button = findViewById(R.id.addRouteButton)
        val sendNotification: Button = findViewById(R.id.sendNotification)
        val addProduct: Button = findViewById(R.id.addProduct)

        addProduct.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        sendNotification.setOnClickListener {
            sendNotificationFunction()
        }

        profileButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
        }

        productButton.setOnClickListener {
            val intent = Intent(this, ProductActivity::class.java)
            startActivity(intent)
        }

        mapButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        accountButton.setOnClickListener {
            val intent = Intent(this, UserAccountActivity::class.java)
            startActivity(intent)
        }

        producerButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        addRouteButton.setOnClickListener {
            val intent = Intent(this, EnterRouteActivity::class.java)
            startActivity(intent)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById<NavigationView>(R.id.drawer_view)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


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
            val sharedPreferencesUserRole = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
            val role = sharedPreferencesUserRole.getString("role", "empty")

            if(role.equals("Dostavljac") || role.equals("Kupac")) {
                bottomNav.add(MeowBottomNavigation.Model(add, R.drawable.round_add_24))
            }
            bottomNav.add(MeowBottomNavigation.Model(notf,R.drawable.notifications_fill0_wght400_grad0_opsz24))
            bottomNav.add(MeowBottomNavigation.Model(account,R.drawable.account_circle_fill0_wght400_grad0_opsz24))

            bottomNav.setOnClickMenuListener {
                when(it.id) {
                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    notf-> {
                        val intent   = Intent(this, NotificationsActivity::class.java)
                        startActivity(intent)
                    }
                    account-> {
                        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                        val role = sharedPreferences.getString("role", "empty")
                        if((role.equals("Kupac") || role.equals("Dostavljač")))
                            startActivity(Intent(this, UserAccountActivity::class.java))
                        else {
                            val intent   = Intent(this, UserProfileActivity::class.java)
                            intent.putExtra("MyProfile", true)
                            startActivity(intent)
                        }
                    }

                }

                if(role.equals("Dostavljac") || role.equals("Kupac")) {
                    when(it.id) {
                        add ->{

                            if (role.equals("Dostavljač")  && !role.equals("Kupac")) {
                                val intent = Intent(this, EnterRouteActivity::class.java)
                                startActivity(intent)
                            }
                            if (!role.equals("Dostavljač") && !role.equals("Kupac")) {
                                val intent = Intent(this, AddProductActivity::class.java)
                                startActivity(intent)
                            }
                        }
                    }
                }
            }


            bottomNav.show(account)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }

    }

    private fun sendNotificationFunction() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/notification"

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "empty")
        val lastName = sharedPreferences.getString("lastName", "empty")
        val email = sharedPreferences.getString("email", "empty")

        val jsonParams = JSONObject()
        jsonParams.put("title", "Notifikacija")
        jsonParams.put("body", "$firstName $lastName je poslao notifikaciju")
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setVisibility() {
        val navView: NavigationView = findViewById(R.id.drawer_view)
        val navMenu: Menu = navView.menu

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val role = sharedPreferences.getString("role", "empty")

        val drawerProfileItem: MenuItem? = navMenu.findItem(R.id.drawer_profile)

        if (drawerProfileItem != null) {
            drawerProfileItem.setVisible(!(role.equals("Kupac") || role.equals("Dostavljač")))
        } else {
            Toast.makeText(this, "Stavka ne postoji", Toast.LENGTH_LONG).show()
        }
    }
}
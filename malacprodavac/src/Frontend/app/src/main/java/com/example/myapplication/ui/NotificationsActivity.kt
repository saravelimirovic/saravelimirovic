package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.adapters.CommentAdapter
import com.example.myapplication.adapters.NotificationAdapter
import com.example.myapplication.models.Comment
import com.example.myapplication.models.NotificationModel
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView

class NotificationsActivity : AppCompatActivity() {
    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        setVisibility()

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

                R.id.drawer_orders -> {
                    startActivity(Intent(this, OrdersActivity::class.java))
                    finish()
                }

                R.id.drawer_delivery_requests -> {
                    startActivity(Intent(this, DeliveryRequests::class.java))
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

        fetchNotifications()

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
            bottomNav.setCount(4, "1")

            bottomNav.setOnClickMenuListener {
                when(it.id) {
                    search -> {
                        val intent   = Intent(this, ExploreActivity::class.java)
                        startActivity(intent)
                    }
                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
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

            bottomNav.show(notf)
        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }
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

    public fun fetchNotifications() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "empty")
        val url = StaticAddress.URL + "/notification/my-notifications/$email"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.length() == 0) {
                    val noNotifications: RelativeLayout = findViewById(R.id.noNotifications)
                    noNotifications.visibility = View.VISIBLE
                    val notificationsListView: ListView = findViewById(R.id.notificationsListView)
                    notificationsListView.visibility = View.GONE
                }
                else {
                    val notificationList: MutableList<NotificationModel> = ArrayList()
                    for (i in 0 until response.length()) {
                        val objekat = response.getJSONObject(i)
                        notificationList.add(
                            NotificationModel(objekat.getLong("id"),
                                objekat.getString("senderFirstName"),
                                objekat.getString("senderLastName"),
                                objekat.getString("dateNotificationIsSent"),
                                objekat.getString("title"),
                                objekat.getString("body"),
                                objekat.getString("receiverEmail")
                            )
                        )
                    }
                    val notifications = notificationList
                    val adapter = NotificationAdapter(this, R.layout.one_notification, notifications, this)
                    val listView: ListView = findViewById(R.id.notificationsListView)
                    listView.adapter = adapter
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                val headers = java.util.HashMap<String, String>()
//
//                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")
//
//                headers["Authorization"] = "Bearer $jwtToken"
//                return headers
//            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)

    }
}
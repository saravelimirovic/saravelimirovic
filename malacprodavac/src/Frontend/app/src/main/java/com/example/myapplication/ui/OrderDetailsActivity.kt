package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.myapplication.R
import com.example.myapplication.adapters.OrderDetailsOneOrderAdapter
import com.example.myapplication.adapters.OrdersAdapter
import com.example.myapplication.databinding.ActivityOneCreditCardBinding
import com.example.myapplication.databinding.ActivityOrderDetailsBinding
import com.example.myapplication.models.Order
import com.example.myapplication.models.OrderDetailsOneOrder
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import java.time.LocalDate
import java.util.HashMap

class OrderDetailsActivity : AppCompatActivity() {

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    private lateinit var binding: ActivityOrderDetailsBinding
    var deliveryOrInPerson: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_details)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton : Button = findViewById(R.id.go_back)
        backButton.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        val orderId = intent.getLongExtra("orderId", 0)
        fetchProducList(orderId)
        fetchCustomer(orderId)



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
                    home -> {
                        val intent   = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

        } catch (e: Exception) {
            println("ne radi :(((((((((((((")
        }

        val findDeliverer: Button = findViewById(R.id.findDeliverer)
        findDeliverer.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            answerOrder(orderId, "Prihvaceno")
                            findDeliverer()
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste pripremili ovu porudžbinu? Pronađite dostavljača.")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

        binding.acceptOrderRequest.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            answerOrder(orderId, "Prihvaceno")
                            val intent   = Intent(this, OrdersActivity::class.java)
                            startActivity(intent)
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste pripremili ovu porudžbinu? Biće obrisana iz liste.")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

        binding.rejectOrderRequest.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            answerOrder(orderId, "Odbijeno")
                            val intent   = Intent(this, OrdersActivity::class.java)
                            startActivity(intent)
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste sigurni da želite da odbijete ovu porudžbinu? Nećete moći da je povratite.")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

        binding.rejectOrderDeliveryRequest.setOnClickListener {
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            answerOrder(orderId, "Odbijeno")
                            val intent   = Intent(this, OrdersActivity::class.java)
                            startActivity(intent)
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste sigurni da želite da odbijete ovu porudžbinu? Nećete moći da je povratite.")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }
    }

    private fun fetchProducList(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/order/productsInOrder/$id"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val orderList: MutableList<OrderDetailsOneOrder> = ArrayList()
                var totalPrice = 0.0
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    orderList.add(OrderDetailsOneOrder(objekat.getLong("productId"),
                        objekat.getString("name"),
                        objekat.getDouble("quantity").toInt(),
                        objekat.getString("measuringUnit"),
                        objekat.getDouble("price"),
                        objekat.getString("image")))
                    totalPrice += objekat.getDouble("price")
                }
                binding.totalPrice.text = totalPrice.toString() + " RSD"

                val orders = orderList
                val adapter = OrderDetailsOneOrderAdapter(this, R.layout.activity_one_ordered_item, orders)
                val listView: ListView = findViewById(R.id.orderItemsListView)
                listView.adapter = adapter
            },
            { error ->
//                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)

    }

    private fun fetchCustomer(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/order/userInOrder/$id"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                val fullName: Editable = Editable.Factory.getInstance().newEditable(response.getString("fullName"))
                binding.name.text = fullName
                binding.kupac.text = fullName
                val cityName: Editable = Editable.Factory.getInstance().newEditable(response.getString("cityName"))
                binding.location.text = cityName
                val streetName: Editable = Editable.Factory.getInstance().newEditable(response.getString("streetName"))
                binding.street.text = streetName
                val phoneNumber: Editable = Editable.Factory.getInstance().newEditable(response.getString("phoneNumber"))
                binding.phone.text = phoneNumber
                val delivery: Editable = Editable.Factory.getInstance().newEditable(response.getBoolean("delivery").toString())
                if (delivery.toString() == "true") {
                    binding.deliveryOrInPerson.text = "Dostava"
                    binding.findDelivererLayout.visibility = View.VISIBLE
                    binding.acceptRefuse.visibility = View.GONE
                }
                else {
                    binding.deliveryOrInPerson.text = "Lično preuzimanje"
                    binding.infoText.visibility = View.GONE
                    binding.infoDiv.visibility = View.GONE
                    binding.findDelivererLayout.visibility = View.GONE
                }
//                val paymentMethod: Editable = Editable.Factory.getInstance().newEditable(response.getString("paymentMethod"))
            },
            { error ->
//                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
                binding.kupac.text = intent.getStringExtra("fullNameCustomer")
                binding.infoText.visibility = View.GONE
                binding.infoDiv.visibility = View.GONE
                binding.deliveryOrInPerson.text = "Lično preuzimanje"
                binding.findDelivererLayout.visibility = View.GONE
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

    private fun answerOrder(orderId: Long, answer: String) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/notification/send-order-answer/$orderId/$answer"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->

            },
            { error ->
            }) {
//            @Throws(AuthFailureError::class)
//            override fun getHeaders(): Map<String, String> {
//                val headers = HashMap<String, String>()
//
//                val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
//                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")
//
//                headers["Authorization"] = "Bearer $jwtToken"
//                return headers
//            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }

    private fun findDeliverer() {
        val intent = Intent(this, AvailableDeliverymanActivity::class.java)
        val splitted =  binding.location.text.toString().split(",")[0]
        intent.putExtra("to", splitted)
        startActivity(intent)
    }
}
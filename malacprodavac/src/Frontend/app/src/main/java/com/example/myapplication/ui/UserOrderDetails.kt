package com.example.myapplication.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
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
import com.example.myapplication.adapters.ProductsInCartAdapter
import com.example.myapplication.databinding.ActivityUserOrderDetailsBinding
import com.example.myapplication.models.MyProfileProduct
import com.example.myapplication.models.ProductInCart
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import org.json.JSONArray
import org.json.JSONObject


class UserOrderDetails : AppCompatActivity() {
    private lateinit var binding: ActivityUserOrderDetailsBinding
    private var totalPrice: Double = 0.0;
    private var street: String = ""
    private var streetNumber: String = ""
    private var city: String = ""
    private var postalCode: String = ""
    private var productIdList: List<Long> = emptyList()
    private var quantityList: List<Double> = emptyList()

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    @SuppressLint("MissingInflatedId", "UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_order_details)
        binding = ActivityUserOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val creditCardName: String = intent.getStringExtra("cardName") ?: "Izaberi karticu"
        val companyId = intent.getLongExtra("companyId", 0)
        binding.cardName.text = creditCardName

        if(!binding.cardName.text.equals("Izaberi karticu")) {
            binding.personalPaymentCheckBox.isChecked = false
            binding.cardPaymentCheckBox.isChecked = true
            binding.layoutPaymentCard.visibility = View.VISIBLE
            binding.pomLayout1.visibility = View.GONE
        }

        val backButton : Button = findViewById(R.id.go_back)
        backButton.setOnClickListener {
            var intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }

        //---------------------- Show list of credit cards --------------------------------//
        var showListOfCreditCards = findViewById<ImageButton>(R.id.showListOffCreditCards)
        showListOfCreditCards.setOnClickListener {
            var intent = Intent(this, CreditCardListActivity::class.java)
            intent.putExtra("fromCart", true)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }

        //---------------------- DELIVERY & PICK UP BUTTON LISTENERS ----------------------//
        var deliveryButton = findViewById<TextView>(R.id.deliveryButton)
        var licnoPreuzimanjeButton = findViewById<TextView>(R.id.licnoPreuzimanjeButton)
        val deliveryLayout = findViewById<LinearLayout>(R.id.deliveryInfoLayout)
        val lpLayout = findViewById<LinearLayout>(R.id.licnoPreuzimanjeLayout)

        val checkBox = findViewById<RadioButton>(R.id.cardPaymentCheckBox)
        checkBox.setOnClickListener { view ->
            if(view is RadioButton) {
                var paymentCard = findViewById<LinearLayout>(R.id.layoutPaymentCard)
                var pomLayout = findViewById<LinearLayout>(R.id.pomLayout1)

                if (view.isChecked) {
                    paymentCard.visibility = View.VISIBLE
                    pomLayout.visibility = View.GONE
                } else {
                    paymentCard.visibility = View.GONE
                    pomLayout.visibility = View.VISIBLE
                }
            }
        }

        binding.personalPaymentCheckBox.setOnClickListener {
            if (binding.personalPaymentCheckBox.isChecked) {
                binding.layoutPaymentCard.visibility = View.GONE
                binding.pomLayout1.visibility = View.VISIBLE
            } else {
                binding.layoutPaymentCard.visibility = View.VISIBLE
                binding.pomLayout1.visibility = View.GONE
            }
        }

        deliveryButton.setOnClickListener {
            deliveryButton.setTextColor(resources.getColor(R.color.white))
            deliveryButton.background = getDrawable(R.drawable.input_field_green)
            deliveryButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.bike_white), null, null, null)
            licnoPreuzimanjeButton.setTextColor(resources.getColor(R.color.black))
            licnoPreuzimanjeButton.background = getDrawable(R.drawable.input_field_white)
            licnoPreuzimanjeButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.walk), null, null, null)
            deliveryLayout.visibility = View.VISIBLE
            lpLayout.visibility = View.GONE
        }

        licnoPreuzimanjeButton.setOnClickListener {
            licnoPreuzimanjeButton.setTextColor(resources.getColor(R.color.white))
            licnoPreuzimanjeButton.background = getDrawable(R.drawable.input_field_green)
            licnoPreuzimanjeButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.walk_white), null, null, null)
            deliveryButton.background = getDrawable(R.drawable.input_field_white)
            deliveryButton.setTextColor(resources.getColor(R.color.black))
            deliveryButton.setCompoundDrawablesWithIntrinsicBounds(getDrawable(R.drawable.bike), null, null, null)
            deliveryLayout.visibility = View.GONE
            lpLayout.visibility = View.VISIBLE
        }
        //------------------------------ FINISH ORDER --------------------------------------------------//
        var orderButton = findViewById<Button>(R.id.finishOrder)
        orderButton.setOnClickListener {
            if(binding.cardName.text.equals("Izaberi karticu") && binding.personalPaymentCheckBox.isChecked == false) {
                Toast.makeText(this, "Izaberite karticu, ili izaberite plaćanje pouzećem.", Toast.LENGTH_LONG).show()
            }
            else if (binding.streetNumber.text.equals("Nemate lokaciju!") && deliveryLayout.visibility == View.VISIBLE) {
                Toast.makeText(this, "Morate uneti svoju lokacju.", Toast.LENGTH_LONG).show()
            }
            else {
                //Toast.makeText(this, "CompanyId: " + companyId, Toast.LENGTH_LONG).show()
                addOrder(companyId)
                sendNotificationFunction(companyId)
                showToastMessage("Uspešno naručeno!", false)
                deleteMyCartForThatCompany(companyId)
            }
        }

        binding.addLocation.setOnClickListener {
            var intent = Intent(this, MyLocationActivity::class.java)
            intent.putExtra("fromCart", true)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }

        fetchProductsInCart(companyId)
        getMyLocation()


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

    }

    private fun addOrder(companyId: Long) {
        val jsonParams = JSONObject()
        if (binding.personalPaymentCheckBox.isChecked == false)
            jsonParams.put("paymentMethod", "card")
        else
            jsonParams.put("paymentMethod", "onDelivery")

        if (binding.deliveryInfoLayout.visibility == View.VISIBLE)
            jsonParams.put("deliveryMethod", "delivery")
        else
            jsonParams.put("deliveryMethod", "inPerson")
        jsonParams.put("streetName", street)
        jsonParams.put("streetNumber", streetNumber)
        jsonParams.put("cityName", city)
        jsonParams.put("status", "ordered")
        jsonParams.put("ownerId", companyId)
        jsonParams.put("productIds", JSONArray(productIdList))
        jsonParams.put("quantities", JSONArray(quantityList))

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/order/add"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                Toast.makeText(this, "Uspešno naručeno!", Toast.LENGTH_LONG).show();
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

    private fun sendNotificationFunction(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/notification/send-order"

        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        val firstName = sharedPreferences.getString("firstName", "empty")
        val lastName = sharedPreferences.getString("lastName", "empty")
        val email = sharedPreferences.getString("email", "empty")

        val jsonParams = JSONObject()
        jsonParams.put("title", "Nova porudžbina")
        jsonParams.put("body", "$firstName $lastName je poslao/la svoju porudžbinu.")
        jsonParams.put("email", email)
        jsonParams.put("to", companyId)
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

    fun fetchProductsInCart(ownerId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/cart/getProductsInCart/$ownerId"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                if(response.length() == 0) {
                    val noCart: RelativeLayout = findViewById(R.id.noCart)
                    noCart.visibility = View.VISIBLE
                    val items: LinearLayout = findViewById(R.id.page)
                    items.visibility = View.GONE
                }
                else {
                    totalPrice = 0.0
                    productIdList = emptyList()
                    quantityList = emptyList()
                    val productList: MutableList<ProductInCart> = ArrayList()
                    for (i in 0 until response.length()) {
                        val objekat = response.getJSONObject(i)
                        productList.add(
                            ProductInCart(objekat.getLong("id"),
                                objekat.getString("image"),
                                objekat.getString("name"),
                                objekat.getString("measuringUnit"),
                                objekat.getDouble("price"),
                                objekat.getDouble("quantity"),
                                objekat.getDouble("quantityInStock")
                            ))
                        totalPrice = totalPrice + objekat.getDouble("price")
                        productIdList = productIdList.toMutableList().plus(objekat.getLong("id"))
                        quantityList = quantityList.toMutableList().plus(objekat.getDouble("quantity"))

                    }

                    binding.totalPrice.text = totalPrice.toInt().toString() + " RSD"
                    val products = productList
                    val adapter = ProductsInCartAdapter(this, R.layout.user_one_ordered_item, products, this, ownerId)
                    val listView: ListView = findViewById(R.id.userOrderItemsListView)
                    listView.adapter = adapter
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

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }

    private fun getMyLocation() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/user/getUpdateLocation"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                street = response.getString("street")
                streetNumber = response.getString("streetNumber")
                city = response.getString("city")
                postalCode = response.getString("postalCode")
                //Toast.makeText(this, "Broj je: " + response.getString("street").toString(), Toast.LENGTH_LONG).show()
                if(response.getString("city").toString().equals("")) {
                    binding.streetNumber.text = "Nemate lokaciju!"
                    binding.cityPostalCode.text = "Unesite lokaciju za dostavu."
                }
                else {
                    binding.streetNumber.text = response.getString("street") + " " + response.getString("streetNumber")
                    binding.cityPostalCode.text = response.getString("city") + " " + response.getString("postalCode")
                }
            },
            { error ->
//                showToastMessage("Došlo je do greške, pokušajte ponovo.", true)
                binding.streetNumber.text = "Nemate lokaciju!"
                binding.cityPostalCode.text = "Unesite lokaciju za dostavu."
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

    private fun deleteMyCartForThatCompany(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/cart/deleteCart/$id"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                var intent = Intent(this, UserProfileActivity::class.java)
                intent.putExtra("companyId", id)
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

    private fun showToastMessage(message: String, isError: Boolean) {
        val toast = Toast.makeText(this@UserOrderDetails, message, Toast.LENGTH_LONG)
        val view = toast.view
        view?.setBackgroundColor(if (isError) android.graphics.Color.RED else android.graphics.Color.GREEN)
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(android.graphics.Color.WHITE)
        toast.show()
    }
}
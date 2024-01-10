package com.example.myapplication.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import com.example.myapplication.adapters.CommentAdapter
import com.example.myapplication.adapters.OrdersAdapter
import com.example.myapplication.adapters.SliderAdapter
import com.example.myapplication.models.Comment
import com.example.myapplication.models.Order
import com.example.myapplication.models.SliderItem
import com.example.myapplication.utils.StaticAddress
import com.google.android.material.navigation.NavigationView
import com.smarteist.autoimageslider.SliderView
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import kotlin.math.abs


class ProductActivity : AppCompatActivity(){

    private var quantity: Double = 10.0
    private var myCompanyId = 0L

    protected final val home = 1
    protected final val search = 2
    protected final val add = 3
    protected final val notf = 4
    protected final val account = 5
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        val increase: Button = findViewById(R.id.increaseQuantityButton)
        val decrease: Button = findViewById(R.id.decreaseQuantityButton)
        val addToCartButton: Button = findViewById(R.id.addToCartButton)
        val ratingBar = findViewById<RatingBar>(R.id.productRating)
        val backButton: Button = findViewById(R.id.backButton)
        val submitCommentButton: Button = findViewById(R.id.submitCommentButton)
        val deleteProductButton: ImageView = findViewById(R.id.deleteProduct)
        val deleteProductCard: CardView = findViewById(R.id.deleteProductCard)
        val goToCart: ImageView = findViewById(R.id.goToCart)
        val goToCartCard: CardView = findViewById(R.id.goToCartCard)
        val quantityTextView: TextView = findViewById(R.id.quantityTextView)
        val productPriceTextView: TextView = findViewById(R.id.productPrice)

        getMyCompanyId()
        val companyId = intent.getLongExtra("companyId", -1)
        val productId = intent.getLongExtra("productId", -1)

        deleteProductButton.setOnClickListener{
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            deleteProduct(productId, companyId) // todo
                        }

                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                }

            val builder: AlertDialog.Builder = AlertDialog.Builder(this)

            builder.setMessage("Da li ste sigurni da želite da obrišete ovaj proizvod? Nećete moći da ga povratite.")
                .setPositiveButton("Da", dialogClickListener)
                .setNegativeButton("Ne", dialogClickListener)
                .show()
        }

        if(myCompanyId.equals(companyId)) {
            deleteProductCard.visibility = View.VISIBLE
            goToCartCard.visibility = View.INVISIBLE
            addToCartButton.visibility = View.INVISIBLE
            increase.visibility = View.INVISIBLE
            decrease.visibility = View.INVISIBLE
            quantityTextView.visibility = View.INVISIBLE
        }
        else {
            deleteProductCard.visibility = View.INVISIBLE
            goToCartCard.visibility = View.VISIBLE
            addToCartButton.visibility = View.VISIBLE
            increase.visibility = View.VISIBLE
            decrease.visibility = View.VISIBLE
            quantityTextView.visibility = View.VISIBLE
        }

        addToCartButton.setOnClickListener {
            if(quantity < (quantityTextView.text.toString().toDouble())) {
                Toast.makeText(this, "Na stanju ima " + quantity.toInt().toString() + " proizvoda.", Toast.LENGTH_LONG).show()
            }
            else {
                val price = productPriceTextView.text.split(" ")[0].toDouble()
//                Toast.makeText(this, "PRICE: " + price, Toast.LENGTH_LONG).show()
                addProduct(companyId, productId, quantityTextView.text.toString().toDouble(),
                    quantityTextView.text.toString().toDouble() * price)
            }
        }

        goToCartCard.setOnClickListener {
            val intent = Intent(this, UserOrderDetails::class.java)
            intent.putExtra("productId", productId)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }

        increase.setOnClickListener {
            val text = quantityTextView.text.toString()
            var br = text.toInt()
            br += 1
            quantityTextView.setText(br.toString())
        }

        decrease.setOnClickListener {
            val text = quantityTextView.text.toString()
            var br = text.toInt()
            br -= 1
            if (br > 0)
                quantityTextView.setText(br.toString())
        }


        backButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }

//        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
//            Toast.makeText(this@ProductActivity, "Ocena: $rating", Toast.LENGTH_LONG).show()
//        }

        submitCommentButton.setOnClickListener {
            addComment(productId)
        }

        fetchProductData(productId)
        fetchComments(productId)

        val cardView: CardView = findViewById(R.id.cardViewProduct)
        cardView.setBackgroundResource(R.drawable.card_shape)




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

    }

//    private fun generateDummyComments(): List<Comment> {
//        val comments: MutableList<Comment> = ArrayList()
//        comments.add(Comment("Petar Petrovic", LocalDate.now(), "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "slika"))
//        comments.add(Comment("Ana Anic", LocalDate.now(), "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "slika"))
//        comments.add(Comment("Mika Mikic", LocalDate.now(), "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "slika"))
//        comments.add(Comment("Marko Markovic", LocalDate.now(), "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", "slika"))
//        return comments
//    }

    private fun addComment(productId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/commentProduct/add"

        val comment: EditText = findViewById(R.id.commentInputField)
        val jsonParams = JSONObject()
        jsonParams.put("productId", productId)
        jsonParams.put("text", comment.text)

        val JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
//                Toast.makeText(this, "Dodat komentar", Toast.LENGTH_LONG).show()
                comment.setText("")
                fetchComments(productId)
            },
            { error ->
//                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        JsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(JsonObjectRequest)
    }


    private fun fetchComments(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/commentProduct/getAllForProduct/$id"

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                val commentList: MutableList<Comment> = ArrayList()
                for (i in 0 until response.length()) {
                    val objekat = response.getJSONObject(i)
                    commentList.add(
                        Comment(objekat.getString("fullName"),
                        objekat.getString("date"),
                        objekat.getString("text")
                    ))
                }

                val comments = commentList
                val adapter = CommentAdapter(this, R.layout.actiivity_comment, comments)
                val listView: ListView = findViewById(R.id.commentsListView)
                listView.adapter = adapter
            },
            { error ->
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)

    }

    private fun fetchProductData(productId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/product/productDetails/$productId"  // Promenite ovo prema stvarnoj putanji na vašem serveru

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                updateProductUI(response)
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

    private fun updateProductUI(response: JSONObject) {
        try {
            val productName = response.getString("productName")
            val productPrice = response.getDouble("productPrice")
            val productTotalRating = response.getDouble("totalRating")
            val productCountRating = response.getInt("ratingCount")
            val ownerFullName = response.getString("ownerFullName")
            val description = response.getString("productDescription")
            this.quantity = response.getDouble("quantity")
            val image = response.getString("image")

            // Base64 string koji predstavlja sliku
            val base64Image = image

            // Dekodiranje Base64 stringa u Bitmap
            val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            val imageView: ImageView = findViewById(R.id.productImageSlider)
            imageView.setImageBitmap(decodedBitmap)


            val nameTextView: TextView = findViewById(R.id.productName)
            val priceTextView: TextView = findViewById(R.id.productPrice)
            val ratingTextView: RatingBar = findViewById(R.id.productRating)
            val averageRatingTextView: TextView = findViewById(R.id.averageRatingTextView)
            val numberOfRatingsTextView: TextView = findViewById(R.id.numberOfRatingsTextView)
            val descriptionTextView: TextView = findViewById(R.id.productDescription)
            val notInStock: TextView = findViewById(R.id.notInStock)
            notInStock.visibility = View.GONE

            if(this.quantity == 0.0) {
                val addToCartButton: Button = findViewById(R.id.addToCartButton)
                addToCartButton.isEnabled = false
                val notInStock: TextView = findViewById(R.id.notInStock)
                notInStock.visibility = View.VISIBLE
            }

            nameTextView.text = productName
            priceTextView.text = "$productPrice RSD"
            ratingTextView.stepSize = 0.1F
            ratingTextView.rating = productTotalRating.toFloat()
            averageRatingTextView.text = productTotalRating.toString()
            numberOfRatingsTextView.text = "($productCountRating ocena)"
            descriptionTextView.text = description

        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun addProduct(ownerId: Long, productId: Long, quantity: Double, productTotal: Double) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/cart/addProduct"

        val jsonParams = JSONObject()
        jsonParams.put("ownerId", ownerId)
        jsonParams.put("productId", productId)
        jsonParams.put("quantity", quantity)
        jsonParams.put("productTotal", productTotal)

        val JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST, url, jsonParams,
            { response ->
                Toast.makeText(this, "Dodato u korpu", Toast.LENGTH_LONG).show()
            },
            { error ->
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        JsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(JsonObjectRequest)
    }

    private fun deleteProduct(productId: Long, companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/product/deleteProduct/$productId"
        //Toast.makeText(this, "Id je : " + productId.toString(), Toast.LENGTH_LONG).show()

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                if (response.getInt("Message") == 1) {
                    val intent = Intent(this, UserProfileActivity::class.java)
                    intent.putExtra("companyId", companyId)
                    startActivity(intent)
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

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }

    public fun getMyCompanyId() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = StaticAddress.URL + "/web/company/myCompany"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                myCompanyId = response.getLong("id")
            },
            { error ->
                myCompanyId = 0L
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = java.util.HashMap<String, String>()

                val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }
}
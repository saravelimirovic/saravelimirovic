package com.example.myapplication.ui

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.adapters.CommentAdapter
import com.example.myapplication.adapters.OrdersAdapter
import com.example.myapplication.adapters.SliderAdapter
import com.example.myapplication.models.Comment
import com.example.myapplication.models.Order
import com.example.myapplication.models.SliderItem
import com.example.myapplication.utils.StaticAddress
import com.smarteist.autoimageslider.SliderView
import org.json.JSONException
import org.json.JSONObject
import java.time.LocalDate
import kotlin.math.abs


class ProductActivity : AppCompatActivity(){

    private var quantity: Double = 10.0

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

        deleteProductButton.setOnClickListener{
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            deleteProduct() // todo
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

        if(profileIsMine()) {
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

        val companyId = intent.getLongExtra("companyId", 0)
        backButton.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra("companyId", companyId)
            startActivity(intent)
        }

//        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
//            Toast.makeText(this@ProductActivity, "Ocena: $rating", Toast.LENGTH_LONG).show()
//        }

        val productId = intent.getLongExtra("productId", 0)

        submitCommentButton.setOnClickListener {
            addComment(productId)
        }

        fetchProductData(productId)
        fetchComments(productId)

        //image slider

        val sliderView: SliderView = findViewById(R.id.productImageSlider)
        val listOfImages = intArrayOf(R.drawable.sijalice_1, R.drawable.sijalice_2, R.drawable.sijalice_3)
        val sliderAdapter= SliderAdapter(listOfImages)
        sliderView.setSliderAdapter(sliderAdapter)

        val cardView: CardView = findViewById(R.id.cardViewProduct)
        cardView.setBackgroundResource(R.drawable.card_shape)
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
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
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

    private fun deleteProduct() {

    }

    private fun profileIsMine(): Boolean {
        return false
    }
}
package com.example.myapplication.adapters

import com.example.myapplication.models.ProductInCart

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.R
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.ui.CreditCardListActivity
import com.example.myapplication.ui.HomeActivity
import com.example.myapplication.ui.UserOrderDetails
import com.example.myapplication.utils.StaticAddress
import kotlin.math.roundToInt


class ProductsInCartAdapter (context: Context, resource: Int, objects: List<ProductInCart>, private val activity: UserOrderDetails, private val companyId: Long) :
    ArrayAdapter<ProductInCart>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.user_one_ordered_item, parent, false)
        }

        val product = getItem(position)

        val textViewName: TextView = itemView!!.findViewById(R.id.name)
        val textViewUnit: TextView = itemView.findViewById(R.id.measuringUnit)
        val textViewPrice: TextView = itemView.findViewById(R.id.price)
        val imageViewProduct: ImageView = itemView!!.findViewById(R.id.imageProduct)
        val textViewQuantity: TextView = itemView.findViewById(R.id.quantityTextView)
        val buttonDelete: Button = itemView.findViewById(R.id.increaseQuantityButton)

        // Base64 string koji predstavlja sliku
        val base64Image = "${product?.image}"

        // Dekodiranje Base64 stringa u Bitmap
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        // Postavljanje dekodiranog Bitmap-a u ImageView
        imageViewProduct.setImageBitmap(decodedBitmap)

        textViewName.text = "${product?.name}"
        textViewUnit.text = "${product?.measuringUnit}"
        val quantity = product?.quantity
        val quantityInStock = product?.quantityInStock
        if (quantityInStock != null) {
            if(quantityInStock < quantity!!) {
                textViewQuantity.text = "${product?.quantityInStock?.toInt()}"
                var priceForOne = product?.price?.toInt()?.div(quantity)
                var newPrice = priceForOne?.times(quantityInStock)
                textViewPrice.text = "${newPrice} RSD"
            }
            else {
                textViewQuantity.text = "${product?.quantity?.toInt()}"
                textViewPrice.text = "${product?.price?.toInt()} RSD"
            }
        }
        val productId = product?.id

        buttonDelete.setOnClickListener {
            if (productId != null) {
                deleteProductFromCart(productId)
            }
        }

        return itemView
    }

    private fun deleteProductFromCart(id: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val url = StaticAddress.URL + "/web/cart/deleteProductFromCart/$id"
        //Toast.makeText(context, "Id je : " + id.toString(), Toast.LENGTH_LONG).show()

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                if (response.getBoolean("Message") == true) {
                    activity.fetchProductsInCart(companyId)
                }
            },
            { error ->

            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()

                val sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                val jwtToken = sharedPreferences.getString("JWT_TOKEN", "token")

                headers["Authorization"] = "Bearer $jwtToken"
                return headers
            }
        }

        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonObjectRequest)
    }
}
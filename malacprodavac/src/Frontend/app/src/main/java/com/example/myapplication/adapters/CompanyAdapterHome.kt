package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.models.CompanyCard
import com.example.myapplication.ui.HomeActivity
import com.example.myapplication.ui.OrderDetailsActivity
import com.example.myapplication.ui.UserProfileActivity
import com.example.myapplication.utils.StaticAddress
import java.util.HashMap

class CompanyAdapterHome(private val context: Context, private val companies: List<CompanyCard>, private val activity: HomeActivity) :
    RecyclerView.Adapter<CompanyAdapterHome.CompanyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.grid_one_product, parent, false)
        return CompanyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val companyCard = companies[position]

        // Postavljanje podataka proizvoda u prikaz
        val resourceId = context.resources.getIdentifier("food", "drawable", context.packageName)
        val liked = context.resources.getIdentifier("favorite_fill1_wght400_grad0_opsz24", "drawable", context.packageName)
        val notLiked = context.resources.getIdentifier("favorite_fill0_wght400_grad0_opsz24", "drawable", context.packageName)

        // Base64 string koji predstavlja sliku
        val base64Image = "${companyCard?.logo}"

        // Dekodiranje Base64 stringa u Bitmap
        val decodedBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

        // Postavljanje dekodiranog Bitmap-a u ImageView
        if (!companyCard.logo.equals("null"))
            holder.productImage.setImageBitmap(decodedBitmap)
        else
            holder.productImage.setImageResource(resourceId)

        if (companyCard.isLiked)
            holder.like.setImageResource(liked)
        else
            holder.like.setImageResource(notLiked)
        holder.rating.rating = companyCard.rating.toFloat()
        holder.name.text = companyCard.name

        holder.itemView.setOnClickListener {
            val intent = Intent(context, UserProfileActivity::class.java)
            intent.putExtra("companyId", companyCard.id)
            context.startActivity(intent)
        }

        holder.like.setOnClickListener {
            if(companyCard.isLiked) {
                unfollowCompany(companyCard.id)
                holder.like.setImageResource(notLiked)
            }
            else {
                followCompany(companyCard.id)
                holder.like.setImageResource(liked)
            }
        }
    }

    override fun getItemCount(): Int {
        return companies.size
    }

    inner class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.grid_image_product)
        val name: TextView = itemView.findViewById(R.id.grid_name_product)
        val like: ImageView = itemView.findViewById(R.id.grid_like_product)
        val rating: RatingBar = itemView.findViewById(R.id.grid_ratingBar_product)
    }

    private fun followCompany(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val url = StaticAddress.URL + "/web/following/addFollow/$companyId"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Toast.makeText(context, "Zapratili ste korisnika.", Toast.LENGTH_LONG).show()
                activity.fetchInNearbyCompanies()
                activity.fetchPopularCompanies()
            },
            { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
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

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }

    private fun unfollowCompany(companyId: Long) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val url = StaticAddress.URL + "/web/following/deleteFollow/$companyId"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                Toast.makeText(context, "Otpratili ste korisnika.", Toast.LENGTH_LONG).show()
                activity.fetchInNearbyCompanies()
                activity.fetchPopularCompanies()
            },
            { error ->
                Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
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

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }

}

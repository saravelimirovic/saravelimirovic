package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.R
import com.example.myapplication.models.Order
import com.example.myapplication.models.OrderDetailsOneOrder
import com.example.myapplication.ui.OrderDetailsActivity
import java.time.format.DateTimeFormatter

class OrdersAdapter(context: Context, resource: Int, objects: List<Order>) :
    ArrayAdapter<Order>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.activity_one_order, parent, false)
        }

        val order = getItem(position)

        val imageViewPerson: ImageView = itemView!!.findViewById(R.id.imagePerson)
        val textViewName: TextView = itemView.findViewById(R.id.name)
        val textViewDate: TextView = itemView.findViewById(R.id.date)
        val textViewLocation: TextView = itemView.findViewById(R.id.location)
        val textViewPrice: TextView = itemView.findViewById(R.id.price)

        val resourceId = context.resources.getIdentifier("avatar", "drawable", context.packageName)
        imageViewPerson.setImageResource(resourceId)
        textViewName.text = "${order?.fullNameCustomer}"
        textViewDate.text = "${order?.dateOrderIsMade}"
        if (order?.deliveryCity == "null")
            textViewLocation.text = "Preuzimanje"
        else
            textViewLocation.text = "${order?.deliveryCity}"
        textViewPrice.text = "${order?.totalPrice} RSD"

        itemView.setOnClickListener {
            val intent = Intent(context, OrderDetailsActivity::class.java)
            intent.putExtra("orderId", order?.orderId)
            intent.putExtra("fullNameCustomer", order?.fullNameCustomer)
            context.startActivity(intent)
        }

        return itemView
    }
}

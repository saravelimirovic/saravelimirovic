package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.Order
import com.example.myapplication.models.OrderDetailsOneOrder
import java.time.format.DateTimeFormatter

class OrderDetailsOneOrderAdapter(context: Context, resource: Int, objects: List<OrderDetailsOneOrder>) :
    ArrayAdapter<OrderDetailsOneOrder>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.activity_one_ordered_item, parent, false)
        }

        val orderedItem = getItem(position)

        val imageViewPerson: ImageView = itemView!!.findViewById(R.id.imageProduct)
        val textViewQuantity: TextView = itemView.findViewById(R.id.quantity)
        val textViewName: TextView = itemView.findViewById(R.id.name)
        val textViewMeasuringUnit: TextView = itemView.findViewById(R.id.measuringUnit)
        val textViewPrice: TextView = itemView.findViewById(R.id.price)

        val resourceId = context.resources.getIdentifier("food", "drawable", context.packageName)
        imageViewPerson.setImageResource(resourceId)

        textViewQuantity.text = "${orderedItem?.quantity}x"
        textViewName.text = orderedItem?.name
        textViewMeasuringUnit.text = "${orderedItem?.measuringUnit}"
        textViewPrice.text = "${orderedItem?.price} RSD"

        return itemView
    }
}
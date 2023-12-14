package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.OrderDetailsOneOrder

class UserOrderDetailsOneOrderAdapter (context: Context, resource: Int, objects: List<OrderDetailsOneOrder>) :
    ArrayAdapter<OrderDetailsOneOrder>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.user_one_ordered_item, parent, false)
            }

            val orderedItem = getItem(position)

            val imageViewPerson: ImageView = itemView!!.findViewById(R.id.imageProduct)
            val textViewQuantity: TextView = itemView.findViewById(R.id.quantityTextView)
            val textViewName: TextView = itemView.findViewById(R.id.name)
            val textViewMeasuringUnit: TextView = itemView.findViewById(R.id.measuringUnit)
            val textViewPrice: TextView = itemView.findViewById(R.id.price)

            val resourceId = context.resources.getIdentifier("kiflica", "drawable", context.packageName)
            imageViewPerson.setImageResource(resourceId)
            imageViewPerson.scaleType = ImageView.ScaleType.FIT_XY

            textViewQuantity.text = "${orderedItem?.quantity}"
            textViewName.text = orderedItem?.name
            textViewMeasuringUnit.text = "${orderedItem?.measuringUnit}"
            textViewPrice.text = "${orderedItem?.price} RSD"

            return itemView
        }

}
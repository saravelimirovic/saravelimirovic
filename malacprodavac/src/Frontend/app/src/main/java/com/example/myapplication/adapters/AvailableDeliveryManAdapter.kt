package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.AvailableDeliveryMan
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.ui.MyCreditCardActivity

class AvailableDeliveryManAdapter (context: Context, resource: Int, objects: List<AvailableDeliveryMan>) :
    ArrayAdapter<AvailableDeliveryMan>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context)
                .inflate(R.layout.activity_one_available_deliveryman, parent, false)
        }

        val availableDeliveryMan = getItem(position)

        val deliveryManName: TextView = itemView!!.findViewById(R.id.deliveryman_name)
        val price: TextView = itemView!!.findViewById(R.id.delivery_price)
        val deliveryManDate: TextView = itemView!!.findViewById(R.id.deliveryman_date)
        val startingRoute: TextView = itemView!!.findViewById(R.id.starting_route)
        val finishRoute: TextView = itemView!!.findViewById(R.id.finish_route)

        deliveryManName.text = availableDeliveryMan?.name
        price.text = availableDeliveryMan?.pricePerKM.toString()
        deliveryManDate.text = availableDeliveryMan?.date.toString()
        startingRoute.text = availableDeliveryMan?.startCity
        finishRoute.text = availableDeliveryMan?.finishCity

        return itemView
    }
}
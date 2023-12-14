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
import com.example.myapplication.models.CreditCardListItem
import com.example.myapplication.ui.MyCreditCardActivity
import com.example.myapplication.ui.OrderDetailsActivity

class CreditCardAdapter(context: Context, resource: Int, objects: List<CreditCardListItem>) :
    ArrayAdapter<CreditCardListItem>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.activity_credit_card_list, parent, false)
        }

        val card = getItem(position)

        val textViewCard: TextView = itemView!!.findViewById(R.id.creditCardName)

        textViewCard.text = card?.nameOnCard

        itemView.setOnClickListener {
            val intent = Intent(context, MyCreditCardActivity::class.java)
            intent.putExtra("id", card?.id)
            context.startActivity(intent)
        }

        return itemView
    }
}
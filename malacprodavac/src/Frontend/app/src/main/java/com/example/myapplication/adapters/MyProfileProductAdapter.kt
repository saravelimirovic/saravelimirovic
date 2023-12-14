package com.example.myapplication.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.MyProfileProduct
import com.example.myapplication.ui.ProductActivity

class MyProfileProductAdapter (context: Context, resource: Int, objects: List<MyProfileProduct>) :
    ArrayAdapter<MyProfileProduct>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.one_product_my_profile_layout, parent, false)
        }

        val product = getItem(position)

        val textViewName: TextView = itemView!!.findViewById(R.id.textView3)
        val textViewDesc: TextView = itemView.findViewById(R.id.description)
        val textViewPrice: TextView = itemView.findViewById(R.id.price)
        val imageViewProduct: ImageView = itemView!!.findViewById(R.id.productImage)

        val resourceId = context.resources.getIdentifier("food", "drawable", context.packageName)
        imageViewProduct.setImageResource(resourceId)
        textViewName.text = "${product?.name}"
        textViewDesc.text = "${product?.description}"
        textViewPrice.text = "${product?.price} RSD"

        itemView.setOnClickListener {
            val intent = Intent(context, ProductActivity::class.java)
            intent.putExtra("productId", product?.id)
            intent.putExtra("companyId", product?.companyId)
            context.startActivity(intent)
        }

        return itemView
    }

}
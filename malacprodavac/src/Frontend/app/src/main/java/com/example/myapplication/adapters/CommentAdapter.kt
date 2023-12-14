package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.Comment
import java.time.format.DateTimeFormatter

class CommentAdapter(context: Context, resource: Int, objects: List<Comment>) :
    ArrayAdapter<Comment>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.actiivity_comment, parent, false)
        }

        val comment = getItem(position)

        val imageViewPerson: ImageView = itemView!!.findViewById(R.id.imageViewPerson)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val textViewComment: TextView = itemView.findViewById(R.id.textViewComment)

        val format = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        val formatedDate = comment?.date?.format(format)

        val resourceId = context.resources.getIdentifier("avatar", "drawable", context.packageName)
        imageViewPerson.setImageResource(resourceId)
        textViewName.text = "${comment?.name}"
        textViewDate.text = formatedDate.toString()
        textViewComment.text = comment?.comment

        return itemView
    }
}

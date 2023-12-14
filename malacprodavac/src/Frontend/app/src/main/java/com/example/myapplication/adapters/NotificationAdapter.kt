package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.models.NotificationModel

class NotificationAdapter(context: Context, resource: Int, objects: List<NotificationModel>) :
    ArrayAdapter<NotificationModel>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.one_notification, parent, false)
        }

        val notification = getItem(position)

        val imageViewPerson: ImageView = itemView!!.findViewById(R.id.personImageNotification)
        val textViewName: TextView = itemView.findViewById(R.id.personNameNotification)
        val textViewDate: TextView = itemView.findViewById(R.id.dateNotification)
        val textViewTitle: TextView = itemView.findViewById(R.id.notification_header)
        val textViewBody: TextView = itemView.findViewById(R.id.notification_description)

        val resourceId = context.resources.getIdentifier("avatar", "drawable", context.packageName)
        imageViewPerson.setImageResource(resourceId)
        textViewName.text = "${notification?.senderFirstName} ${notification?.senderLastName}"
        textViewDate.text = notification?.dateNotificationIsSent
        textViewTitle.text = notification?.title
        textViewBody.text = notification?.body

        return itemView
    }
}
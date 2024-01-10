package com.example.myapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.android.volley.AuthFailureError
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.myapplication.R
import com.example.myapplication.models.NotificationModel
import com.example.myapplication.ui.NotificationsActivity
import com.example.myapplication.utils.StaticAddress

class NotificationAdapter(context: Context, resource: Int, objects: List<NotificationModel>, private val activity: NotificationsActivity) :
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
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteNotification)

        val resourceId = context.resources.getIdentifier("avatar", "drawable", context.packageName)
        imageViewPerson.setImageResource(resourceId)
        textViewName.text = "${notification?.senderFirstName} ${notification?.senderLastName}"
        textViewDate.text = notification?.dateNotificationIsSent
        textViewTitle.text = notification?.title
        textViewBody.text = notification?.body

        deleteButton.setOnClickListener {
            deleteNotification(notification?.id)
        }

        return itemView
    }

    private fun deleteNotification(id: Long?) {
        val requestQueue: RequestQueue = Volley.newRequestQueue(context)
        val url = StaticAddress.URL + "/notification/deleteNotification/$id"

        val jsonArrayRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            { response ->
                if (response.getBoolean("Message") == true) {
                    activity.fetchNotifications()
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

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(0, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        requestQueue.add(jsonArrayRequest)
    }
}
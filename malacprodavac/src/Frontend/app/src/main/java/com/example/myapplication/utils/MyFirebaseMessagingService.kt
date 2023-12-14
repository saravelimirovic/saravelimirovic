package com.example.myapplication.utils

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Proverite da li poruka sadrži podatke
        remoteMessage.data.isNotEmpty().let {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            // Ovde možete implementirati dodatnu logiku za obradu podataka
        }

        // Proverite da li poruka sadrži notifikaciju
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
            // Prikazivanje notifikacije
            showNotification(it.title, it.body)
        }
    }

    @SuppressLint("MissingPermission")
    private fun showNotification(title: String?, message: String?) {
        val channelId = "default_channel_id"
        createNotificationChannel(channelId, "Default Channel")

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(notificationId++, builder.build())
        }
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "MyFirebaseMessaging"
        private var notificationId = 0
    }
}

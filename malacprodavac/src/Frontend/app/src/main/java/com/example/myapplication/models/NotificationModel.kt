package com.example.myapplication.models

data class NotificationModel (
    val id: Long,
    val senderFirstName: String,
    val senderLastName: String,
    val dateNotificationIsSent: String,
    val title: String,
    val body: String,
    val receiverEmail: String
)
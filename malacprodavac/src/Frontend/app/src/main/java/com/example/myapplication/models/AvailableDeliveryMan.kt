package com.example.myapplication.models

import java.time.DateTimeException
import java.util.Date

data class AvailableDeliveryMan(
    val userImage: String,
    val userId: Long,
    val firstNameUser: String,
    val lastNameUser: String,
    val from: String,
    val to: String,
    val date: String,
    val time: String
)

package com.example.myapplication.models

import java.time.DateTimeException
import java.util.Date

data class AvailableDeliveryMan(
    val name: String,
    val imgSrc: String,
    val date: Date,
    val startCity: String,
    val finishCity: String,
    val pricePerKM: Float
)

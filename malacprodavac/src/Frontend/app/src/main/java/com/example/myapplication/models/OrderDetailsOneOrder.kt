package com.example.myapplication.models


data class OrderDetailsOneOrder (
    val productId: Long,
    val name: String,
    val quantity: Int,
    val measuringUnit: String,
    val price: Double,
    val image: String
)
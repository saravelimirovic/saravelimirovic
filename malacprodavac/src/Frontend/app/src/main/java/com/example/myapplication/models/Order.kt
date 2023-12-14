package com.example.myapplication.models

data class Order (
    val orderId: Long,
    val fullNameCustomer: String,
    val deliveryCity: String,
    val dateOrderIsMade: String,
    val totalPrice: Double
)
package com.example.myapplication.models

class ProductInCart (
    val id: Long,
    val image: String,
    val name: String,
    val measuringUnit: String,
    val price: Double,
    val quantity: Double,
    val quantityInStock: Double
)
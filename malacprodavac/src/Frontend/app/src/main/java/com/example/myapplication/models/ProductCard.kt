package com.example.myapplication.models


data class ProductCard (
    val name: String,
    val price: Double,
    val rating: Double,
    val isLiked: Boolean,
    val imageResId: String
)
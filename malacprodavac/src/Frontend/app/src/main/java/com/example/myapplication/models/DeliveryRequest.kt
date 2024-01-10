package com.example.myapplication.models

data class DeliveryRequest (
    val id: Long,
    val image: String,
    val companyName: String,
    val firstNameUser: String,
    val lastNameUser: String,
    val cityStart: String,
    val cityEnd: String,
    val date: String,
    val time: String
)
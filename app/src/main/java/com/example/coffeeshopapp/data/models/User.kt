package com.example.coffeeshopapp.data.models

data class User(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val qrCodeNumber: String,
    val points: Double
)

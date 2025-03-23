package com.example.coffeeshopapp.data.models

data class PointsTransaction(
    val userId: Long,
    val amount: Double,
    val description: String
)
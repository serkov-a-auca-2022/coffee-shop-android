package com.example.coffeeshopapp

data class Product(
    val id: Long?,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String,
    val imageUrl: String?
)

package com.example.coffeeshopapp.data.models

data class Notification(
    val id: Long,
    val userId: Long,
    val title: String?,
    val message: String?,
    val timestamp: String?
)

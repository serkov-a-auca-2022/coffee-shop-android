package com.example.coffeeshopapp.data.models

data class NewsItem(
    val id: Long,
    val title: String,
    val shortDescription: String?,
    val content: String,
    val imageUrl: String?,
    val dateTime: String
)

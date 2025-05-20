package com.example.coffeeshopapp.data.models

data class OrderRequest(
    val userId: Long,
    val items: List<OrderItemDto>
)

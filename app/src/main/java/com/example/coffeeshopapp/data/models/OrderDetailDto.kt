package com.example.coffeeshopapp.data.models

import com.google.gson.annotations.SerializedName

/**
 * Полные данные по одному заказу
 */
data class OrderDetailDto(
    @SerializedName("orderId")
    val orderId: Long,

    @SerializedName("finalAmount")
    val totalAmount: Double,  // в бэкенде finalAmount — это та сумма, что нужно показать

    @SerializedName("orderDate")
    val orderDate: String,    // если нужно, можно показать дату на экране детали

    @SerializedName("items")
    val items: List<OrderDetailItemDto>
)

data class OrderDetailItemDto(
    @SerializedName("productId")
    val productId: Long,

    @SerializedName("name")
    val name: String,          // в JSON это поле называется "name"

    @SerializedName("quantity")
    val quantity: Int
)

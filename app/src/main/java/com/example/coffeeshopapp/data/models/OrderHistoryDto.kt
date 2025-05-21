package com.example.coffeeshopapp.data.models

import com.google.gson.annotations.SerializedName

/**
 * Элемент списка «История заказов»
 */
data class OrderHistoryDto(
    @SerializedName("orderId")
    val orderId: Long,

    @SerializedName("orderDate")
    val orderDate: String,  // ISO-строка, напр. "2025-05-15T18:04:39"

    @SerializedName("totalAmount")
    val totalAmount: Double,

    @SerializedName("itemsCount")
    val itemsCount: Int
)
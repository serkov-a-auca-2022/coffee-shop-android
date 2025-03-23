package com.example.coffeeshopapp.data.models


import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class PointsHistory(
    val id: Long,
    val userId: Long,
    val amount: Double,
    val type: String,  // "add" или "deduct"
    val description: String,

    @SerializedName("timestamp")
    val timestamp: String // Дата и время операции, сначала принимаем, потом преобразуем в Date
) {
    fun getFormattedTimestamp(): Date? {
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            format.parse(timestamp)
        } catch (e: Exception) {
            null
        }
    }
}
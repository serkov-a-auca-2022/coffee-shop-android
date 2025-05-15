package com.example.coffeeshopapp.data.models

/**
 * Ответ от сервера для экрана лояльности:
 * - points           — текущее количество баллов
 * - freeDrinks       — сколько бесплатных напитков доступно
 * - totalDrinks      — сколько напитков всего куплено
 * - drinksToNextFree — сколько осталось до следующего бесплатного напитка
 */
data class LoyaltySummary(
    val points: Double,
    val freeDrinks: Int,
    val totalDrinks: Long,
    val drinksToNextFree: Long
)

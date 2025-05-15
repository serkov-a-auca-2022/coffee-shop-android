package com.example.coffeeshopapp.data.models

/** Запрос на логирование «визита» с N напитками */
data class VisitRequest(
    val drinkCount: Int
)

/** Ответ от сервера: сколько всего напитков и сколько free‐drinks */
data class VisitResponse(
    val totalDrinks: Long,
    val freeDrinks: Int
)

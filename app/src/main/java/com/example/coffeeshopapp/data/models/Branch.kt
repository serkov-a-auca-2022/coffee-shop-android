package com.example.coffeeshopapp.data.models

import java.time.LocalTime

data class Branch(
    val id: Int,
    val name: String,      // Ориентировочное название/сленговое имя
    val address: String,
    val openTime: LocalTime,  // Время открытия
    val closeTime: LocalTime, // Время закрытия
    val imageUrl: String      // URL изображения филиала
) {
    // Функция вычисления, открыт ли филиал
    fun isOpenNow(currentTime: LocalTime): Boolean {
        // Предположим, что филиал работает в пределах одного дня:
        return currentTime.isAfter(openTime) && currentTime.isBefore(closeTime)
    }
}

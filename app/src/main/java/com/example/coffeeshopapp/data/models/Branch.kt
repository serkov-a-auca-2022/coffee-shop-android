// app/src/main/java/com/example/coffeeshopapp/data/models/Branch.kt
package com.example.coffeeshopapp.data.models

import java.time.LocalTime

/**
 * Поля должны точно совпадать с тем, что отдаёт ваш бэкенд:
 * {
 *   "id":1,
 *   "name":"Дружба",
 *   "address":"…",
 *   "openTime":"07:00:00",
 *   "closeTime":"23:00:00",
 *   "imageUrl":"https://…"
 * }
 */
data class Branch(
    val id: Int,
    val name: String,
    val address: String,
    val openTime: String,
    val closeTime: String,
    val imageUrl: String
) {
    /** Проверяем, сейчас между openTime и closeTime */
    fun isOpenNow(now: LocalTime): Boolean {
        val open = runCatching { LocalTime.parse(openTime) }.getOrNull() ?: return false
        val close = runCatching { LocalTime.parse(closeTime) }.getOrNull() ?: return false
        return !now.isBefore(open) && !now.isAfter(close)
    }
}

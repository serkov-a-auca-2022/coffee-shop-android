package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.models.Notification
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface LoyaltyApiService {

    /**
     * Добавить визит (POST /api/loyalty/visits/{userId})
     */
    @POST("loyalty/visits/{userId}")
    fun addVisit(@Path("userId") userId: Long): Call<String>

    /**
     * Получить все уведомления пользователя (GET /api/loyalty/notifications/{userId})
     */
    @GET("notifications/user/{userId}")
    fun getUserNotifications(@Path("userId") userId: Long): Call<List<Notification>>
}

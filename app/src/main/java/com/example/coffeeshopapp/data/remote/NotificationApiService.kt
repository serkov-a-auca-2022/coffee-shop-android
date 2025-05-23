package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.models.Notification
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface NotificationApiService {
    /**
     * Получить все уведомления пользователя
     * (маршрут подставляется точно так же, как в вашем NotificationController на бэке)
     */
    @GET("notifications/user/{userId}")
    fun getUserNotifications(@Path("userId") userId: Long): Call<List<Notification>>
}

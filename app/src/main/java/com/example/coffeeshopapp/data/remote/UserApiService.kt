package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.models.LoyaltySummary
import com.example.coffeeshopapp.data.models.PointsHistory
import com.example.coffeeshopapp.data.models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface UserApiService {
    @POST("users/register")
    fun registerUser(@Body user: User): Call<User>

    @GET("users/{phone}")
    suspend fun getUser(@Path("phone") phone: String): Response<User>

    @GET("points/history/{userId}")
    suspend fun getPointsHistory(@Path("userId") userId: Long): List<PointsHistory>

    @GET("loyalty/summary/{userId}")
    suspend fun getLoyaltySummary(@Path("userId") userId: Long): LoyaltySummary

    /** История баллов, собранная из заказов */
    @GET("orders/user/{userId}/points")
    suspend fun getPointsHistoryFromOrders(
        @Path("userId") userId: Long
    ): List<PointsHistory>

    companion object {
        private var INSTANCE: UserApiService? = null

        fun getInstance(): UserApiService {
            if (INSTANCE == null) {
                INSTANCE = Retrofit.Builder()
                    .baseUrl("https://coffee-shop-backend-coffeeshopapp.up.railway.app/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(UserApiService::class.java)
            }
            return INSTANCE!!
        }
    }
}

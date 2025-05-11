package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.models.PointsHistory
import com.example.coffeeshopapp.data.models.PointsTransaction
import com.example.coffeeshopapp.data.models.User
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {
    @POST("users/register")
    fun registerUser(@Body user: User): Call<User>

    @GET("users/{phone}")
    suspend fun getUser(@Path("phone") phone: String): Response<User>


    @POST("points/add")
    fun addPoints(@Body transaction: PointsTransaction): Call<String>

    @POST("points/deduct")
    fun deductPoints(@Body transaction: PointsTransaction): Call<String>

    @GET("points/history/{userId}")
    suspend fun getPointsHistory(@Path("userId") userId: Long): List<PointsHistory>

    companion object {
        private var INSTANCE: UserApiService? = null

        fun getInstance(): UserApiService {
            if (INSTANCE == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://coffee-shop-backend-coffeeshopapp.up.railway.app/api/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                INSTANCE = retrofit.create(UserApiService::class.java)
            }
            return INSTANCE!!
        }
    }
}

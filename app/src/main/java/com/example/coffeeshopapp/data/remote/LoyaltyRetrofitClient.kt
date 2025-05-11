package com.example.coffeeshopapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoyaltyRetrofitClient {

    private const val BASE_URL = "https://coffee-shop-backend-coffeeshopapp.up.railway.app/api/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: LoyaltyApiService by lazy {
        retrofit.create(LoyaltyApiService::class.java)
    }
}

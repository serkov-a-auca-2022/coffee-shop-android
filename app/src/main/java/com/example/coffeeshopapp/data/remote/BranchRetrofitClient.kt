// app/src/main/java/com/example/coffeeshopapp/data/remote/BranchRetrofitClient.kt
package com.example.coffeeshopapp.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://coffee-shop-backend-coffeeshopapp.up.railway.app/api/"

object BranchRetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: BranchApiService = retrofit.create(BranchApiService::class.java)
}

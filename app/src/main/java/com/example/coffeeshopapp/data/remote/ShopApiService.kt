package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.models.OrderRequest
import com.example.coffeeshopapp.data.models.OrderResponse
import com.example.coffeeshopapp.Product
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ShopApiService {
    // получить список категорий
    @GET("categories")
    suspend fun getCategories(): List<String>

    // получить товары по категории
    @GET("products/category/{cat}")
    suspend fun getProductsByCategory(@Path("cat") category: String): List<Product>

    // отправить предзаказ
    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): OrderResponse

    companion object {
        private const val BASE = "https://coffee-shop-backend-coffeeshopapp.up.railway.app/api/"
        val instance: ShopApiService by lazy {
            Retrofit.Builder()
                .baseUrl(BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ShopApiService::class.java)
        }
    }
}

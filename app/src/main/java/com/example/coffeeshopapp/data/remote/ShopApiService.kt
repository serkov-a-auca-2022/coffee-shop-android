package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.Product
import com.example.coffeeshopapp.data.models.OrderDetailDto
import com.example.coffeeshopapp.data.models.OrderHistoryDto
import com.example.coffeeshopapp.data.models.OrderRequest
import com.example.coffeeshopapp.data.models.OrderResponse
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

    // получить один товар по ID
    @GET("products/{id}")
    suspend fun getProduct(@Path("id") productId: Long): Product

    // отправить предзаказ
    @POST("orders")
    suspend fun createOrder(@Body request: OrderRequest): OrderResponse

    @GET("orders/user/{userId}")
    suspend fun getOrdersByUser(@Path("userId") userId: Long): List<OrderHistoryDto>

    /** Детали одного заказа */
    @GET("orders/{orderId}")
    suspend fun getOrderDetail(@Path("orderId") orderId: Long): OrderDetailDto

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

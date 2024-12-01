package com.example.coffeeshopapp

import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("api/products")
    fun getProducts(): Call<List<Product>>

    @POST("api/products")
    fun addProduct(@Body product: Product): Call<Product>

    @PUT("api/products/{id}")
    fun updateProduct(@Path("id") id: Long, @Body product: Product): Call<Product>

    @DELETE("api/products/{id}")
    fun deleteProduct(@Path("id") id: Long): Call<Void>
}

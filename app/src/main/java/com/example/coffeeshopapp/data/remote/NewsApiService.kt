package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.models.NewsItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface NewsApiService {
    @GET("api/news")
    fun getAllNews(): Call<List<NewsItem>>

    @POST("api/news")
    fun createNews(@Body news: NewsItem): Call<NewsItem>

}

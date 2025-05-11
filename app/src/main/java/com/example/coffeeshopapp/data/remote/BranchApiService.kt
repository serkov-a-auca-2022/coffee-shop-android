// app/src/main/java/com/example/coffeeshopapp/data/remote/BranchApiService.kt
package com.example.coffeeshopapp.data.remote

import com.example.coffeeshopapp.data.models.Branch
import retrofit2.Response
import retrofit2.http.GET

interface BranchApiService {
    @GET("branches")
    suspend fun getAll(): Response<List<Branch>>
}

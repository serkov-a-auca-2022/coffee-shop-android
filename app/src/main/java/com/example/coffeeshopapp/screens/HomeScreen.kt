package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.Product
import com.example.coffeeshopapp.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun HomeScreen() {
    val products = remember { mutableStateListOf<Product>() }

    LaunchedEffect(Unit) {
        RetrofitClient.instance.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                response.body()?.let { products.addAll(it) }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("HomeScreen", "Failed to fetch products", t)
            }
        })
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(products) { product ->
            Text(text = "${product.name}: ${product.price}")
        }
    }
}

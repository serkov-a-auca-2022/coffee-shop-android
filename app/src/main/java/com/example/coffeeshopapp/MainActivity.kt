package com.example.coffeeshopapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.coffeeshopapp.ui.theme.CoffeeShopAppTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeShopAppTheme {
                ProductListScreen()
            }
        }
    }
}

@Composable
fun ProductListScreen() {
    val products = remember { mutableStateListOf<Product>() }

    LaunchedEffect(Unit) {
        RetrofitClient.instance.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(
                call: Call<List<Product>>,
                response: Response<List<Product>>
            ) {
                response.body()?.let {
                    products.addAll(it)
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("MainActivity", "Failed to fetch products", t)
            }
        })
    }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        items(products) { product ->
            Text(text = "${product.name}: ${product.price}")
        }
    }
}
package com.example.coffeeshopapp.screens

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.coffeeshopapp.Product
import com.example.coffeeshopapp.data.remote.ShopApiService

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuProductListScreen(
    navController: NavController,
    categoryArg: String
) {
    val category = Uri.decode(categoryArg)
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }
    var error    by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(category) {
        try {
            products = ShopApiService.instance.getProductsByCategory(category)
        } catch (t: Throwable) {
            error = "Не удалось загрузить товары"
        }
    }

    if (error != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
        return
    }
    if (products.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement   = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(products, key = { it.id!! }) { p ->
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .aspectRatio(1f)
                    .clickable {
                        // навигация в детали с категорией + id
                        navController.navigate("menu/${Uri.encode(category)}/product/${p.id}")
                    }
            ) {
                Column(Modifier.fillMaxSize()) {
                    AsyncImage(
                        model              = p.imageUrl,
                        contentDescription = p.name,
                        modifier           = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale       = ContentScale.Crop
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text     = p.name,
                        style    = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
        }
    }
}

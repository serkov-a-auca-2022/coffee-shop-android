package com.example.coffeeshopapp.screens

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshopapp.data.remote.ShopApiService

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MenuScreen(navController: NavController) {
    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var error      by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            categories = ShopApiService.instance.getCategories()
        } catch (t: Throwable) {
            error = "Не удалось загрузить категории"
        }
    }

    Box(Modifier.fillMaxSize()) {
        when {
            error != null -> {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            }
            categories.isEmpty() -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories, key = { it }) { cat ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    val encoded = Uri.encode(cat)
                                    navController.navigate("menu/$encoded")
                                }
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(cat, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

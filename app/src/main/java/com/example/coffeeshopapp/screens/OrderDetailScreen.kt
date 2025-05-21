package com.example.coffeeshopapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshopapp.data.models.OrderDetailDto
import com.example.coffeeshopapp.data.remote.ShopApiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController, orderId: Long) {
    var detail  by remember { mutableStateOf<OrderDetailDto?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(orderId) {
        detail = try {
            ShopApiService.instance.getOrderDetail(orderId)
        } catch (_: Throwable) {
            null
        }
        loading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Заказ №$orderId") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { inner ->
        Box(Modifier.fillMaxSize().padding(inner), contentAlignment = Alignment.Center) {
            if (loading) {
                CircularProgressIndicator()
            } else {
                detail?.let { ord ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Дата: ${ord.orderDate}", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        ord.items.forEach { it ->
                            Text("${it.quantity} × ${it.name}", style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(Modifier.height(12.dp))
                        Text("Итого: ${ord.totalAmount} сом", style = MaterialTheme.typography.titleMedium)
                    }
                } ?: Text("Не удалось загрузить заказ", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

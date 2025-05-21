package com.example.coffeeshopapp.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshopapp.UserPreferences
import com.example.coffeeshopapp.data.models.OrderHistoryDto
import com.example.coffeeshopapp.data.remote.ShopApiService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(navController: NavController) {
    val ctx   = LocalContext.current
    val prefs = remember { UserPreferences(ctx) }
    val user  = prefs.getUser() ?: return

    var orders     by remember { mutableStateOf<List<OrderHistoryDto>>(emptyList()) }
    var isLoading  by remember { mutableStateOf(true) }

    LaunchedEffect(user.id) {
        orders = try {
            ShopApiService.instance.getOrdersByUser(user.id)
        } catch (_: Throwable) {
            emptyList()
        }
        isLoading = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text("История заказов") }) }) { inner ->
        Box(Modifier.fillMaxSize().padding(inner)) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else if (orders.isEmpty()) {
                Text("Нет заказов", Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding    = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(orders) { ord ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .clickable { navController.navigate("orderDetail/${ord.orderId}") },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Заказ №${ord.orderId}", style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text("Дата: ${ord.orderDate}", style = MaterialTheme.typography.bodySmall)
                                Spacer(Modifier.height(4.dp))
                                Text("Сумма: ${ord.totalAmount} сом", style = MaterialTheme.typography.bodyMedium)
                                Text("Позиции: ${ord.itemsCount}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

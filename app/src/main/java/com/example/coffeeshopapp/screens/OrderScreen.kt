package com.example.coffeeshopapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.models.OrderItemDto
import com.example.coffeeshopapp.data.models.OrderRequest
import com.example.coffeeshopapp.Product
import com.example.coffeeshopapp.data.remote.ShopApiService
import com.example.coffeeshopapp.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(onOrderPlaced: () -> Unit) {
    val scope = rememberCoroutineScope()
    val ctx   = LocalContext.current
    val prefs = remember { UserPreferences(ctx) }

    var categories by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCat by remember { mutableStateOf<String?>(null) }
    var products    by remember { mutableStateOf<List<Product>>(emptyList()) }
    val cart        = remember { mutableStateMapOf<Long, Int>() }

    // load categories once
    LaunchedEffect(Unit) {
        categories  = ShopApiService.instance.getCategories()
        selectedCat = categories.firstOrNull()
    }
    // when category changes, fetch its products
    LaunchedEffect(selectedCat) {
        selectedCat?.let {
            products = ShopApiService.instance.getProductsByCategory(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // —— REPLACE ScrollableTabRow WITH LazyRow ——
        LazyRow(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { cat ->
                Tab(
                    selected = (cat == selectedCat),
                    onClick  = { selectedCat = cat },
                    modifier = Modifier.wrapContentWidth()
                ) {
                    Text(
                        text     = cat,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style    = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // product list
        LazyColumn(
            modifier            = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(products) { p ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        modifier             = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment    = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AsyncImage(
                            model              = p.imageUrl,
                            contentDescription = p.name,
                            modifier           = Modifier.size(64.dp)
                        )
                        Column(Modifier.weight(1f)) {
                            Text(p.name, style = MaterialTheme.typography.titleMedium)
                            p.description?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${p.price} сом", style = MaterialTheme.typography.bodyMedium)
                        }
                        val count = cart[p.id] ?: 0
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton({
                                if (count > 0) cart[p.id!!] = count - 1
                            }) { Text("-", style = MaterialTheme.typography.titleLarge) }
                            Text(
                                text      = count.toString(),
                                textAlign = TextAlign.Center,
                                modifier  = Modifier.width(24.dp)
                            )
                            IconButton({
                                cart[p.id!!] = count + 1
                            }) { Text("+", style = MaterialTheme.typography.titleLarge) }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        // send order
        val total = products.sumOf { p -> (cart[p.id] ?: 0) * p.price }
        Button(
            onClick = {
                scope.launch {
                    val user = prefs.getUser() ?: return@launch
                    val items = cart.entries
                        .filter { it.value > 0 }
                        .map { OrderItemDto(it.key, it.value) }
                    ShopApiService.instance.createOrder(
                        OrderRequest(user.id, items)
                    )
                    onOrderPlaced()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Отправить заказ — ${total.toInt()} сом")
        }
    }
}

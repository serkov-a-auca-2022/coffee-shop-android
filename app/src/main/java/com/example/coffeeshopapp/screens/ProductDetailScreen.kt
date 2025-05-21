package com.example.coffeeshopapp.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    categoryArg: String,
    productId: Long
) {
    val category = Uri.decode(categoryArg)
    var product  by remember { mutableStateOf<Product?>(null) }
    var error    by remember { mutableStateOf<String?>(null) }
    val scope    = rememberCoroutineScope()

    LaunchedEffect(category) {
        scope.launch {
            try {
                val all = ShopApiService.instance.getProductsByCategory(category)
                product = all.firstOrNull{ it.id == productId }
                if (product == null) error = "Товар не найден"
            } catch (t: Throwable) {
                error = "Не удалось загрузить товар"
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(product?.name ?: "Загрузка…") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { inner ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(inner),
            contentAlignment = Alignment.Center
        ) {
            when {
                error != null -> Text(error!!, color = MaterialTheme.colorScheme.error)
                product == null -> CircularProgressIndicator()
                else -> Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AsyncImage(
                        model = product!!.imageUrl,
                        contentDescription = product!!.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )
                    Text(product!!.name,        style = MaterialTheme.typography.headlineSmall)
                    Text("${product!!.price} сом", style = MaterialTheme.typography.bodyLarge)
                    product!!.description?.let {
                        Text(it, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

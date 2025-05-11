// app/src/main/java/com/example/coffeeshopapp/screens/BranchesScreen.kt
package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.models.Branch
import com.example.coffeeshopapp.data.remote.BranchRetrofitClient
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.ErrorRed
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BranchesScreen() {
    val coroutineScope = rememberCoroutineScope()
    var branches by remember { mutableStateOf<List<Branch>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf("") }

    // Текущее время в Бишкеке
    val now = LocalTime.now(ZoneId.of("Asia/Bishkek"))

    // Функция «загрузить филиалы»
    fun loadBranches() {
        coroutineScope.launch {
            isLoading = true
            errorMsg = ""
            try {
                val resp = BranchRetrofitClient.api.getAll()
                if (resp.isSuccessful) {
                    branches = resp.body().orEmpty()
                } else {
                    errorMsg = "Ошибка загрузки: ${resp.code()}"
                }
            } catch (e: Exception) {
                Log.e("BranchesScreen", "Ошибка сети", e)
                errorMsg = "Сетевая ошибка: ${e.localizedMessage}"
            }
            isLoading = false
        }
    }

    // При первом заходе — дергаем загрузку
    LaunchedEffect(Unit) {
        loadBranches()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Филиалы", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CoffeeBrown,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = LightBeige
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                errorMsg.isNotEmpty() -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(errorMsg, color = ErrorRed)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { loadBranches() },  // просто вызываем нашу функцию
                            colors = ButtonDefaults.buttonColors(containerColor = Golden)
                        ) {
                            Text("Повторить", color = DarkBrown)
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(branches) { branch ->
                            BranchItem(branch, now)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BranchItem(branch: Branch, now: LocalTime) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Brush.verticalGradient(listOf(Cream, LightBeige)))
                .padding(16.dp)
        ) {
            AsyncImage(
                model = branch.imageUrl,
                contentDescription = branch.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(12.dp))
            Text(branch.name, style = MaterialTheme.typography.titleLarge, color = CoffeeBrown)
            Spacer(Modifier.height(4.dp))
            Text(branch.address, style = MaterialTheme.typography.bodyMedium, color = DarkBrown)
            Spacer(Modifier.height(4.dp))
            // Обрезаем секунды, чтобы было "07:00 – 23:00"
            val t1 = branch.openTime.take(5)
            val t2 = branch.closeTime.take(5)
            Text(
                "Время работы: $t1 – $t2",
                style = MaterialTheme.typography.bodySmall,
                color = DarkBrown
            )
            Spacer(Modifier.height(8.dp))
            val isOpen = branch.isOpenNow(now)
            val statusColor = if (isOpen) CoffeeBrown else ErrorRed
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    if (isOpen) "Открыто" else "Закрыто",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

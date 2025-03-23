package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshopapp.UserPreferences
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.models.PointsHistory
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import com.example.coffeeshopapp.utils.generateQrCodeBitmap
import kotlinx.coroutines.launch

@Composable
fun QrScreen(navController: NavController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var points by remember { mutableStateOf(0.0) }
    var history by remember { mutableStateOf<List<PointsHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    fun loadUserData() {
        coroutineScope.launch {
            Log.d("QrScreen", "Начинаем загрузку данных...")
            isLoading = true
            user = userPreferences.getUser()

            if (user == null) {
                Log.d("QrScreen", "Пользователь не найден в локальном хранилище.")
                isLoading = false
                return@launch
            }

            try {
                //  Загружаем баллы пользователя
                val response = UserRetrofitClient.api.getUser(user!!.phone)
                if (response.isSuccessful) {
                    points = response.body()?.points ?: 0.0
                    Log.d("QrScreen", "Баллы пользователя: $points")
                } else {
                    Log.e("QrScreen", "Ошибка загрузки баллов: ${response.code()}")
                }

                //  Загружаем историю начислений и списаний баллов
                history = UserRetrofitClient.api.getPointsHistory(user!!.id)
                Log.d("QrScreen", "Загружено ${history.size} записей истории.")

            } catch (e: Exception) {
                Log.e("QrScreen", "Ошибка загрузки данных: ${e.message}")
            } finally {
                isLoading = false
                Log.d("QrScreen", "Загрузка данных завершена.")
            }
        }
    }

    LaunchedEffect(Unit) {
        loadUserData()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (user == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Для использования QR-кода, войдите в аккаунт", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("login") }) {
                Text("Войти")
            }
        }
    } else {
        val qrBitmap = remember(user!!.qrCodeNumber) { generateQrCodeBitmap(user!!.qrCodeNumber) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Мой QR", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            Image(bitmap = qrBitmap.asImageBitmap(), contentDescription = "QR-код")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Баллы: $points", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Text("История баллов", style = MaterialTheme.typography.titleMedium)

            if (history.isEmpty()) {
                Text("История баллов пуста", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(history) { transaction ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (transaction.type == "add") MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "${if (transaction.type == "add") "+" else "-"} ${transaction.amount} баллов",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(text = transaction.description, style = MaterialTheme.typography.bodyMedium)
                                Text(text = transaction.timestamp.toString(), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

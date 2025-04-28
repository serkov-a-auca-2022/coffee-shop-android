package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshopapp.UserPreferences
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.models.PointsHistory
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.ErrorRed
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige
import com.example.coffeeshopapp.utils.generateQrCodeBitmap
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
                val response = UserRetrofitClient.api.getUser(user!!.phone)
                if (response.isSuccessful) {
                    points = response.body()?.points ?: 0.0
                    Log.d("QrScreen", "Баллы пользователя: $points")
                } else {
                    Log.e("QrScreen", "Ошибка загрузки баллов: ${response.code()}")
                }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой QR", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CoffeeBrown,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = LightBeige
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (user == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Для использования QR-кода, войдите в аккаунт",
                    style = MaterialTheme.typography.bodyLarge,
                    color = DarkBrown
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Golden,
                        contentColor = DarkBrown
                    )
                ) {
                    Text("Войти", style = MaterialTheme.typography.labelLarge)
                }
            }
        } else {
            val qrBitmap = remember(user!!.qrCodeNumber) { generateQrCodeBitmap(user!!.qrCodeNumber) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Cream, LightBeige)
                        )
                    ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Cream, LightBeige)
                                )
                            )
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            bitmap = qrBitmap.asImageBitmap(),
                            contentDescription = "QR-код",
                            modifier = Modifier
                                .size(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Баллы: $points",
                            style = MaterialTheme.typography.titleLarge,
                            color = CoffeeBrown
                        )
                    }
                }

                Text(
                    text = "История баллов",
                    style = MaterialTheme.typography.titleLarge,
                    color = CoffeeBrown
                )

                if (history.isEmpty()) {
                    Text(
                        text = "История баллов пуста",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkBrown
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(history) { transaction ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateContentSize(),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (transaction.type == "add") Cream else Color(0xFFFFE0E0)
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "${if (transaction.type == "add") "+" else "-"} ${transaction.amount} баллов",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (transaction.type == "add") CoffeeBrown else ErrorRed
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = transaction.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = DarkBrown
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = transaction.timestamp.toString(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = DarkBrown
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
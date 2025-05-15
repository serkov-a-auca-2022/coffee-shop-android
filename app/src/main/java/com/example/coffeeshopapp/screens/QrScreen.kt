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
import com.example.coffeeshopapp.data.models.LoyaltySummary
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.models.PointsHistory
import com.example.coffeeshopapp.data.remote.UserApiService
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
    val ctx   = LocalContext.current
    val prefs = remember { UserPreferences(ctx) }

    var user      by remember { mutableStateOf<User?>(null) }
    var summary   by remember { mutableStateOf<LoyaltySummary?>(null) }
    var history   by remember { mutableStateOf<List<PointsHistory>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    fun loadAll() = scope.launch {
        isLoading = true
        user = prefs.getUser()
        if (user == null) {
            isLoading = false
            return@launch
        }
        try {
            summary = UserApiService.getInstance()
                .getLoyaltySummary(user!!.id)
            history = UserApiService.getInstance()
                .getPointsHistory(user!!.id)
        } catch (e: Exception) {
            Log.e("QrScreen", "Ошибка загрузки", e)
        }
        isLoading = false
    }

    LaunchedEffect(Unit) { loadAll() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мой QR") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CoffeeBrown,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = LightBeige
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                user == null -> {
                    Column(
                        Modifier.fillMaxSize().padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Войдите в аккаунт для QR", color = DarkBrown)
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { navController.navigate("login") }) {
                            Text("Войти")
                        }
                    }
                }
                summary != null -> {
                    val s = summary!!
                    // теперь просто используем totalDrinks напрямую
                    val progressCount = s.totalDrinks.toInt()

                    val qrBitmap = remember(s) {
                        generateQrCodeBitmap(user!!.qrCodeNumber)
                    }

                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(listOf(Cream, LightBeige)))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // QR + цифры
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column(
                                Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Image(
                                    bitmap = qrBitmap.asImageBitmap(),
                                    contentDescription = "QR",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(Modifier.height(12.dp))
                                Text("${s.points.toInt()} баллов", color = CoffeeBrown)
                                Text("Бесплатных: ${s.freeDrinks}", color = CoffeeBrown)
                                Text("Прогресс: $progressCount/6", color = DarkBrown)
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = (progressCount / 6f),
                                    Modifier.fillMaxWidth().height(8.dp)
                                )
                            }
                        }

                        // История баллов
                        Text("История баллов", style = MaterialTheme.typography.titleLarge, color = CoffeeBrown)
                        if (history.isEmpty()) {
                            Text("История пуста", color = DarkBrown)
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(history) { txn ->
                                    Card(
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateContentSize(),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (txn.type == "add") Cream
                                            else Color(0xFFFFE0E0)
                                        )
                                    ) {
                                        Column(Modifier.padding(16.dp)) {
                                            Text(
                                                text = "${if (txn.type == "add") "+" else "-"} ${txn.amount} баллов",
                                                color = if (txn.type == "add") CoffeeBrown else Color.Red
                                            )
                                            Spacer(Modifier.height(4.dp))
                                            Text(txn.description ?: "", color = DarkBrown)
                                            Spacer(Modifier.height(4.dp))
                                            Text(txn.timestamp ?: "", style = MaterialTheme.typography.bodySmall, color = DarkBrown)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
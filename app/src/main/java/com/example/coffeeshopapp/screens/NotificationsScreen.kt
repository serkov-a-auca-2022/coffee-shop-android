package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshopapp.UserPreferences
import com.example.coffeeshopapp.data.models.Notification
import com.example.coffeeshopapp.data.remote.LoyaltyRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { UserPreferences(context) }

    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadNotifications() {
        val user = prefs.getUser()
        if (user == null) {
            errorMessage = "Пользователь не залогинен"
            return
        }

        LoyaltyRetrofitClient.api
            .getUserNotifications(user.id)
            .enqueue(object : Callback<List<Notification>> {
                override fun onResponse(
                    call: Call<List<Notification>>,
                    response: Response<List<Notification>>
                ) {
                    if (response.isSuccessful) {
                        notifications = response.body().orEmpty()
                        errorMessage = ""
                    } else {
                        errorMessage = "Ошибка загрузки: ${response.code()}"
                    }
                }
                override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                    errorMessage = "Ошибка сети: ${t.message}"
                }
            })
    }

    // Загрузка при старте
    LaunchedEffect(Unit) {
        loadNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Уведомления") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            if (notifications.isEmpty()) {
                Text("Уведомлений пока нет")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(notifications) { notif ->
                        Card(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    text = notif.title ?: "(нет заголовка)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = notif.message ?: "(пусто)",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = notif.timestamp ?: "",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

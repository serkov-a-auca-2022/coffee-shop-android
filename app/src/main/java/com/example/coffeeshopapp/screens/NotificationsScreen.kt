package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val userPreferences = remember { UserPreferences(context) }
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    // Функция для загрузки уведомлений
    fun loadNotifications() {
        val user = userPreferences.getUser()
        if (user == null) {
            errorMessage = "Пользователь не залогинен"
            return
        }
        LoyaltyRetrofitClient.api.getUserNotifications(user.id).enqueue(object : Callback<List<Notification>> {
            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                if (response.isSuccessful) {
                    notifications = response.body().orEmpty()
                    errorMessage = ""
                } else {
                    errorMessage = "Ошибка загрузки уведомлений: ${response.code()}"
                }
            }
            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                errorMessage = "Ошибка сети: ${t.message}"
            }
        })
    }

    // Загружаем уведомления при входе
    LaunchedEffect(Unit) {
        loadNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Уведомления") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (notifications.isEmpty()) {
                Text("Уведомлений пока нет")
            } else {
                LazyColumn {
                    items(notifications) { notif ->
                        Card(modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                val safeTitle = notif.title?.takeIf { it.isNotBlank() } ?: "(нет заголовка)"
                                Text(safeTitle, style = MaterialTheme.typography.titleMedium)

                                Spacer(Modifier.height(4.dp))

                                val safeMessage = notif.message?.takeIf { it.isNotBlank() } ?: "(пусто)"
                                Text(safeMessage, style = MaterialTheme.typography.bodyMedium)

                                Spacer(Modifier.height(4.dp))

                                val safeTimestamp = notif.timestamp ?: ""
                                Text(safeTimestamp, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

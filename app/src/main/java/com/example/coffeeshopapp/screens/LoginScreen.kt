package com.example.coffeeshopapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (User) -> Unit,
    onSwitchToRegister: () -> Unit
)
 {
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Вход", style = MaterialTheme.typography.headlineSmall)

        TextField(
            value = phone,
            onValueChange = {
                if (it.length <= 10 && it.matches(Regex("^0[0-9]{0,9}$"))) {
                    phone = it
                }
            },
            label = { Text("Телефон") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
        }

        Button(onClick = {
            coroutineScope.launch {
                try {
                    val response = UserRetrofitClient.api.getUser(phone)
                    if (response.isSuccessful) {
                        val loggedInUser = response.body()!!
                        onLoginSuccess(loggedInUser)

                        // Смена вкладки на профиль ( был баг с постоянной переадрисацией)
                        navController.navigate("profile") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    } else {
                        errorMessage = "Номер не найден"
                    }
                } catch (e: Exception) {
                    errorMessage = "Ошибка подключения"
                }
            }
        }) {
            Text("Войти")
        }



        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onSwitchToRegister) {
            Text("Нет аккаунта? Зарегистрируйтесь")
        }
    }
}

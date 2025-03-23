package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import com.example.coffeeshopapp.*
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoginScreen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Проверяем сохранённый номер телефона
    LaunchedEffect(Unit) {
        val savedPhone = userPreferences.getPhone()
        if (savedPhone.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val response = UserRetrofitClient.api.getUser(savedPhone)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            user = it
                            userPreferences.saveUser(it)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ProfileScreen", "Ошибка загрузки профиля", e)
                } finally {
                    isLoading = false
                }
            }
        } else {
            isLoading = false
            isLoginScreen = true // Если нет пользователя — показываем вход
        }
    }

    if (isLoading) {
        CircularProgressIndicator()
    } else {
        if (user == null) {
            if (isLoginScreen) {
                LoginScreen(
                    navController = navController, // Передаём navController
                    onLoginSuccess = { loggedInUser ->
                        user = loggedInUser
                        userPreferences.saveUser(loggedInUser)
                    },
                    onSwitchToRegister = { isLoginScreen = false }
                )
            }  else {
                RegisterScreen(
                    onRegisterSuccess = { newUser ->
                        user = newUser
                        userPreferences.saveUser(newUser)
                    },
                    onSwitchToLogin = { isLoginScreen = true }
                )
            }
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Профиль", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Имя: ${user!!.firstName}")
                Text(text = "Фамилия: ${user!!.lastName}")
                if (!user!!.email.isNullOrEmpty()) {
                    Text(text = "Email: ${user!!.email}")
                }
                Text(text = "Телефон: ${user!!.phone}")

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    userPreferences.clearUser()
                    user = null
                    isLoginScreen = true
                }) {
                    Text("Выйти")
                }
            }
        }
    }
}

package com.example.coffeeshopapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun RegisterScreen(onRegisterSuccess: (User) -> Unit, onSwitchToLogin: () -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Регистрация", style = MaterialTheme.typography.headlineSmall)

        TextField(value = firstName, onValueChange = { firstName = it }, label = { Text("Имя") })
        TextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Фамилия") })
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email (необязательно)") })
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
            val newUser = User(0, firstName, lastName, email, phone, "", 0.0)
            UserRetrofitClient.api.registerUser(newUser).enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        onRegisterSuccess(response.body() ?: newUser)
                    } else {
                        errorMessage = "Этот номер уже зарегистрирован"
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    errorMessage = "Ошибка подключения"
                }
            })
        }) {
            Text("Зарегистрироваться")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onSwitchToLogin) {
            Text("У меня уже есть аккаунт")
        }
    }
}

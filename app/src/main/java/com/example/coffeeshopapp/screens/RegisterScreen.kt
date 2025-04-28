package com.example.coffeeshopapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.ErrorRed
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: (User) -> Unit, onSwitchToLogin: () -> Unit) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBeige)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Cream, LightBeige)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Логотип
            Icon(
                painter = painterResource(id = R.drawable.profile_icon_background), // TODO: Добавить ресурс логотипа
                contentDescription = "Логотип кофейни",
                modifier = Modifier.size(80.dp),
                tint = CoffeeBrown
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Регистрация",
                style = MaterialTheme.typography.headlineSmall,
                color = CoffeeBrown
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Имя") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = CoffeeBrown,
                    unfocusedBorderColor = Cream,
                    cursorColor = Golden
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Фамилия") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = CoffeeBrown,
                    unfocusedBorderColor = Cream,
                    cursorColor = Golden
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email (необязательно)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = CoffeeBrown,
                    unfocusedBorderColor = Cream,
                    cursorColor = Golden
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    if (it.length <= 10 && it.matches(Regex("^0[0-9]{0,9}$"))) {
                        phone = it
                    }
                },
                label = { Text("Номер телефона") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = CoffeeBrown,
                    unfocusedBorderColor = Cream,
                    cursorColor = Golden
                )
            )

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
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
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Golden,
                    contentColor = DarkBrown
                )
            ) {
                Text("Зарегистрироваться", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onSwitchToLogin,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = CoffeeBrown
                )
            ) {
                Text("У меня уже есть аккаунт")
            }
        }
    }
}
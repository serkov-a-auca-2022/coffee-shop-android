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
import androidx.navigation.NavController
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.ErrorRed
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    onLoginSuccess: (User) -> Unit,
    onSwitchToRegister: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

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
                text = "Вход",
                style = MaterialTheme.typography.headlineSmall,
                color = CoffeeBrown
            )
            Spacer(modifier = Modifier.height(24.dp))

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
                    coroutineScope.launch {
                        try {
                            val response = UserRetrofitClient.api.getUser(phone)
                            if (response.isSuccessful) {
                                val loggedInUser = response.body()!!
                                onLoginSuccess(loggedInUser)
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
                Text("Войти", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onSwitchToRegister,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = CoffeeBrown
                )
            ) {
                Text("Нет аккаунта? Зарегистрируйтесь")
            }
        }
    }
}
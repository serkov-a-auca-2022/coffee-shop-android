package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.coffeeshopapp.R
import com.example.coffeeshopapp.UserPreferences
import com.example.coffeeshopapp.data.models.User
import com.example.coffeeshopapp.data.remote.UserRetrofitClient
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoginScreen by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

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
            isLoginScreen = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Профиль", style = MaterialTheme.typography.headlineSmall) },
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
        } else {
            if (user == null) {
                if (isLoginScreen) {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = { loggedInUser ->
                            user = loggedInUser
                            userPreferences.saveUser(loggedInUser)
                        },
                        onSwitchToRegister = { isLoginScreen = false }
                    )
                } else {
                    RegisterScreen(
                        onRegisterSuccess = { newUser ->
                            user = newUser
                            userPreferences.saveUser(newUser)
                        },
                        onSwitchToLogin = { isLoginScreen = true }
                    )
                }
            } else {
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
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(CoffeeBrown, Cream)
                                    )
                                )
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.profile_icon_background),
                                contentDescription = "Аватар",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Cream)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "${user!!.firstName} ${user!!.lastName}",
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (user!!.email.isNotEmpty()) {
                                    Text(
                                        text = user!!.email,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = "Телефон: ${user!!.phone}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            userPreferences.clearUser()
                            user = null
                            isLoginScreen = true
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
                        Text("Выйти", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}
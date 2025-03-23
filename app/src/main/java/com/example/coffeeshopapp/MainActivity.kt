package com.example.coffeeshopapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.coffeeshopapp.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeShopApp()
        }
    }
}

/**
 * Навигация и нижнее меню (Scaffold).
 */
@Composable
fun CoffeeShopApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHostContainer(navController, Modifier.padding(paddingValues))
    }
}

/**
 * Контейнер для NavHost (все страницы подключены здесь).
 */
@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home", // Стартовый экран
        modifier = modifier
    ) {
        composable("home") { HomeScreen() }
        composable("branches") { BranchesScreen() }
        composable("qr") { QrScreen(navController) }
        composable("news") { NewsScreen() }
        composable("profile") { ProfileScreen(navController) }
        composable("login") {
            LoginScreen(
                navController = navController, // Передача контроллера
                onLoginSuccess = { user -> navController.navigate("profile") },
                onSwitchToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { user ->
                    navController.navigate("profile")
                },
                onSwitchToLogin = {
                    navController.navigate("login")
                }
            )
        }
    }
}


/**
 * Нижняя навигационная панель.
 */
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        "home" to "Главная",
        "branches" to "Филиалы",
        "qr" to "Мой QR",
        "news" to "Новости",
        "profile" to "Профиль"
    )

    NavigationBar(containerColor = Color.LightGray) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { (route, title) ->
            NavigationBarItem(
                label = { Text(title) },
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { }
            )
        }
    }
}

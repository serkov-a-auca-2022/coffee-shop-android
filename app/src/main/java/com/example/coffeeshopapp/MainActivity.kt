package com.example.coffeeshopapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.coffeeshopapp.screens.*
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoffeeShopApp()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CoffeeShopApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHostContainer(navController, Modifier.padding(paddingValues))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen(navController) }
        composable("branches") { BranchesScreen() }
        composable("qr") { QrScreen(navController) }
        composable("news") { NewsScreen(navController) }
        composable("profile") { ProfileScreen(navController) }
        composable("login") {
            LoginScreen(
                navController = navController,
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
        composable("notifications") { NotificationsScreen(navController) }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Triple("home", "Главная", Icons.Default.Home),
        Triple("branches", "Филиалы", Icons.Default.Storefront),
        Triple("qr", "Мой QR", Icons.Default.QrCode),
        Triple("news", "Новости", Icons.Default.Newspaper),
        Triple("profile", "Профиль", Icons.Default.Person)
    )

    NavigationBar(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(CoffeeBrown, Cream)
                )
            ),
        containerColor = androidx.compose.ui.graphics.Color.Transparent,
        tonalElevation = 0.dp
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { (route, title, icon) ->
            val isSelected = currentRoute == route
            val iconColor by animateColorAsState(
                label = "iconColorAnimation",
                targetValue = if (isSelected) Golden else LightBeige,
                animationSpec = tween(300)
            )
            val textColor by animateColorAsState(
                label = "textColorAnimation",
                targetValue = if (isSelected) Golden else LightBeige,
                animationSpec = tween(300)
            )

            NavigationBarItem(
                label = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        color = textColor
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Golden,
                    unselectedIconColor = LightBeige,
                    selectedTextColor = Golden,
                    unselectedTextColor = LightBeige,
                    indicatorColor = Cream.copy(alpha = 0.3f)
                )
            )
        }
    }
}
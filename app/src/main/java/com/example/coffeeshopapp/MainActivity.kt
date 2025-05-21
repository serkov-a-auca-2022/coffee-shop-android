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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.coffeeshopapp.screens.*
import com.example.coffeeshopapp.screens.OrderScreen
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CoffeeShopApp() }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CoffeeShopApp() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        NavHostContainer(navController, Modifier.padding(padding))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavHostContainer(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier) {
        composable("home")     { HomeScreen(navController) }
        composable("branches") { BranchesScreen() }
        composable("order")    { OrderScreen { navController.popBackStack() } }
        composable("qr")       { QrScreen(navController) }
        composable("news")     { NewsScreen(navController) }
        composable("profile")  { ProfileScreen(navController) }
        composable("login")    { LoginScreen(navController,
            onLoginSuccess    = { navController.navigate("profile") },
            onSwitchToRegister= { navController.navigate("register") })
        }
        composable("register") { RegisterScreen(
            onRegisterSuccess = { navController.navigate("profile") },
            onSwitchToLogin   = { navController.navigate("login") })
        }
        composable("notifications") { NotificationsScreen(navController) }

        // — Меню категорий
        composable("menu") {
            MenuScreen(navController)
        }

        // — Список товаров в категории
        composable(
            "menu/{category}",
            arguments = listOf(navArgument("category"){ type = NavType.StringType })
        ) { back ->
            MenuProductListScreen(
                navController,
                back.arguments!!.getString("category")!!
            )
        }

        // — Детали товара (категория + id)
        composable(
            "menu/{category}/product/{id}",
            arguments = listOf(
                navArgument("category"){ type = NavType.StringType },
                navArgument("id"){ type = NavType.LongType }
            )
        ) { back ->
            ProductDetailScreen(
                navController,
                back.arguments!!.getString("category")!!,
                back.arguments!!.getLong("id")
            )
        }

        composable("history") { OrderHistoryScreen(navController) }
        composable(
            "orderDetail/{orderId}",
            arguments = listOf(navArgument("orderId") { type = NavType.LongType })
        ) { back ->
            OrderDetailScreen(navController, back.arguments!!.getLong("orderId"))
        }

    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        Triple("home","Главная",Icons.Default.Home),
        Triple("branches","Филиалы",Icons.Default.Storefront),
        Triple("qr","Мой QR",Icons.Default.QrCode),
        Triple("news","Новости",Icons.Default.Newspaper),
        Triple("profile","Профиль",Icons.Default.Person)
    )
    NavigationBar(
        modifier = Modifier
            .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(Brush.verticalGradient(listOf(CoffeeBrown, Cream))),
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .0f),
        tonalElevation = 0.dp
    ) {
        val current = navController.currentBackStackEntryAsState().value?.destination?.route
        items.forEach { (route, title, icon) ->
            val selected = current == route
            val iconColor = animateColorAsState(if (selected) Golden else LightBeige, tween(300)).value
            val textColor = animateColorAsState(if (selected) Golden else LightBeige, tween(300)).value

            NavigationBarItem(
                icon = {
                    Icon(icon, title, tint = iconColor, modifier = Modifier.size(if (selected) 28.dp else 24.dp))
                },
                label = { Text(title, color = textColor) },
                selected = selected,
                onClick  = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = Golden,
                    unselectedIconColor = LightBeige
                )
            )
        }
    }
}
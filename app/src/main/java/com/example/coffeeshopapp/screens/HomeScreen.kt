// app/src/main/java/com/example/coffeeshopapp/screens/HomeScreen.kt
package com.example.coffeeshopapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige

data class Drink(val name: String, val imageUrl: String)

val sampleDrinks = listOf(
    Drink("Капучино", "https://images.unsplash.com/photo-1517256064527-09c73fc2a2f8"),
    Drink("Латте",   "https://images.unsplash.com/photo-1541167760496-1628856ab772"),
    Drink("Эспрессо","https://images.unsplash.com/photo-1494314675227-a88d57b826f7")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coffee Haven", style = MaterialTheme.typography.headlineSmall) },
                actions = {
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(Icons.Default.Notifications, "Уведомления", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CoffeeBrown,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = LightBeige
    ) { inner ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // — Banner (unchanged) —
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(CoffeeBrown, Cream)))) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1498804103079-a6351b050096",
                        contentDescription = "Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Попробуйте наш новый сезонный кофе!",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.BottomStart).padding(16.dp)
                    )
                }
            }

            // — Buttons Row —
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick    = { navController.navigate("menu") },
                    modifier   = Modifier.weight(1f).height(48.dp)
                ) {
                    Text("Меню", fontSize = 16.sp)
                }
                Button(
                    onClick    = { navController.navigate("order") },
                    modifier   = Modifier.weight(1f).height(48.dp),
                    colors     = ButtonDefaults.buttonColors(containerColor = Golden, contentColor = DarkBrown)
                ) {
                    Text("Заказать сейчас", fontSize = 16.sp)
                }
            }

            // — Popular carousel (unchanged) —
            Text("Популярное", style = MaterialTheme.typography.titleLarge, color = CoffeeBrown)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(sampleDrinks) { drink ->
                    Card(
                        modifier = Modifier.width(120.dp).clickable { /* TODO */ },
                        shape    = RoundedCornerShape(12.dp),
                        elevation= CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            Modifier.background(LightBeige).padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model              = drink.imageUrl,
                                contentDescription = drink.name,
                                modifier           = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale       = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(drink.name, style = MaterialTheme.typography.titleMedium, color = CoffeeBrown)
                        }
                    }
                }
            }
        }
    }
}

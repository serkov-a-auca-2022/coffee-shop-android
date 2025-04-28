package com.example.coffeeshopapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = CoffeeBrown,
    secondary = Cream,
    tertiary = Golden,
    background = LightBeige,
    surface = LightBeige,
    onPrimary = Color.White,
    onSecondary = DarkBrown,
    onTertiary = DarkBrown,
    onBackground = DarkBrown,
    onSurface = DarkBrown,
    error = ErrorRed
)

private val DarkColorScheme = darkColorScheme(
    primary = CoffeeBrown,
    secondary = Cream,
    tertiary = Golden,
    background = DarkBrown,
    surface = DarkBrown,
    onPrimary = Color.White,
    onSecondary = LightBeige,
    onTertiary = LightBeige,
    onBackground = LightBeige,
    onSurface = LightBeige,
    error = ErrorRed
)

@Composable
fun CoffeeShopAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Отключаем динамические цвета для консистентности
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
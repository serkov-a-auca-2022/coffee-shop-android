package com.example.coffeeshopapp.screens

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.coffeeshopapp.data.models.NewsItem
import com.example.coffeeshopapp.data.remote.NewsRetrofitClient
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.ErrorRed
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var newsList by remember { mutableStateOf<List<NewsItem>>(emptyList()) }
    var selectedNews by remember { mutableStateOf<NewsItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    fun loadNews() {
        isLoading = true
        NewsRetrofitClient.api.getAllNews().enqueue(object : Callback<List<NewsItem>> {
            override fun onResponse(
                call: Call<List<NewsItem>>,
                response: Response<List<NewsItem>>
            ) {
                if (response.isSuccessful) {
                    newsList = response.body().orEmpty()
                    errorMessage = ""
                } else {
                    errorMessage = "Ошибка загрузки новостей: ${response.code()}"
                }
                isLoading = false
            }

            override fun onFailure(call: Call<List<NewsItem>>, t: Throwable) {
                errorMessage = "Сетевая ошибка: ${t.message}"
                isLoading = false
            }
        })
    }

    LaunchedEffect(Unit) {
        loadNews()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Новости", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CoffeeBrown,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = LightBeige
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                errorMessage.isNotEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = errorMessage,
                            color = ErrorRed,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { loadNews() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Golden,
                                contentColor = DarkBrown
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Попробовать снова")
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(newsList) { newsItem ->
                            NewsCard(newsItem) { selectedNews = it }
                        }
                    }
                }
            }
        }
    }

    if (selectedNews != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedNews = null },
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            ),
            containerColor = LightBeige
        ) {
            NewsDetailSheet(news = selectedNews!!) { selectedNews = null }
        }
    }
}

@Composable
fun NewsCard(newsItem: NewsItem, onClick: (NewsItem) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { onClick(newsItem) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Cream, LightBeige)
                    )
                )
        ) {
            if (!newsItem.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(newsItem.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = newsItem.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = newsItem.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = CoffeeBrown
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (!newsItem.shortDescription.isNullOrEmpty()) {
                    Text(
                        text = newsItem.shortDescription,
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkBrown
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = newsItem.dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkBrown
                )
            }
        }
    }
}

@Composable
fun NewsDetailSheet(news: NewsItem, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = news.title,
                style = MaterialTheme.typography.headlineSmall,
                color = CoffeeBrown
            )
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Закрыть",
                    tint = CoffeeBrown
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (news.dateTime.isNotEmpty()) {
            Text(
                text = news.dateTime,
                style = MaterialTheme.typography.bodySmall,
                color = DarkBrown
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        if (!news.imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = news.imageUrl,
                contentDescription = news.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = news.content,
            style = MaterialTheme.typography.bodyMedium,
            color = DarkBrown
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}
package com.example.coffeeshopapp.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.coffeeshopapp.data.models.Branch
import com.example.coffeeshopapp.ui.theme.CoffeeBrown
import com.example.coffeeshopapp.ui.theme.Cream
import com.example.coffeeshopapp.ui.theme.DarkBrown
import com.example.coffeeshopapp.ui.theme.ErrorRed
import com.example.coffeeshopapp.ui.theme.Golden
import com.example.coffeeshopapp.ui.theme.LightBeige
import java.time.LocalTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
val sampleBranches = listOf(
    Branch(
        id = 1,
        name = "Дружба",
        address = "Байтик баатыра 39, г. Бишкек",
        openTime = LocalTime.of(7, 0),
        closeTime = LocalTime.of(23, 0),
        imageUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085" // Реальное фото кофейни
    ),
    Branch(
        id = 2,
        name = "Медерова",
        address = "Медерова 81, г. Бишкек",
        openTime = LocalTime.of(8, 0),
        closeTime = LocalTime.of(22, 0),
        imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a3cbb"
    ),
    Branch(
        id = 3,
        name = "Боконбаева",
        address = "Боконбаева 101, г. Бишкек",
        openTime = LocalTime.of(7, 30),
        closeTime = LocalTime.of(23, 0),
        imageUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BranchesScreen() {
    val currentTime = LocalTime.now(ZoneId.of("Asia/Bishkek"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Филиалы", style = MaterialTheme.typography.headlineSmall) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CoffeeBrown,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = LightBeige
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sampleBranches) { branch ->
                BranchItem(branch, currentTime)
            }
        }
    }
}

@Composable
fun BranchItem(branch: Branch, currentTime: LocalTime) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
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
                .padding(16.dp)
        ) {
            AsyncImage(
                model = branch.imageUrl,
                contentDescription = "Фото филиала ${branch.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = branch.name,
                style = MaterialTheme.typography.titleLarge,
                color = CoffeeBrown
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = branch.address,
                style = MaterialTheme.typography.bodyMedium,
                color = DarkBrown
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Сегодня: ${branch.openTime} – ${branch.closeTime}",
                style = MaterialTheme.typography.bodySmall,
                color = DarkBrown
            )
            Spacer(modifier = Modifier.height(8.dp))
            val statusText = if (branch.isOpenNow(currentTime)) "Открыто" else "Закрыто"
            val statusColor = if (branch.isOpenNow(currentTime)) Color(0xFF4CAF50) else ErrorRed
            Surface(
                modifier = Modifier
                    .align(Alignment.End)
                    .clip(RoundedCornerShape(8.dp)),
                color = statusColor.copy(alpha = 0.1f)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}
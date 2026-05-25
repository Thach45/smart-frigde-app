package com.example.android_app.ui.sceens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.android_app.ui.theme.ErrorColor
import com.example.android_app.ui.theme.OnPrimary
import com.example.android_app.ui.theme.Primary
import com.example.android_app.ui.theme.PrimaryContainer
import com.example.android_app.ui.theme.SurfaceContainer
import com.example.android_app.ui.theme.SurfaceContainerLowest
import com.example.android_app.ui.theme.Tertiary
import com.example.android_app.ui.theme.TertiaryContainer
import com.example.android_app.ui.theme.TextOnSurface
import com.example.android_app.ui.theme.TextSecondary
import com.example.android_app.ui.theme.WarningColor
import com.example.android_app.ui.theme.WarningText


// Mock Data
data class FoodItem(
    val name: String,
    val quantity: String,
    val progress: Float,
    val statusText: String,
    val statusColor: Color,
    val statusTextColor: Color = statusColor,
    val badgeIcon: String,
    val imageUrl: String
)

val mockFoodItems = listOf(
    FoodItem("Trứng gà", "10 quả", 0.8f, "Còn 12 ngày", Tertiary, Tertiary, "🌱", "https://lh3.googleusercontent.com/aida-public/AB6AXuBQlZA8DVr0kdjLBmZGu6hUU4KqDXZ13s2TwhfuCmiERqmoWd_myf1xNDUh7GA-HYFcZqmH8xihMiplUWcJ-DhJkPalcooTk8v3D7RR_Gxzm1t1jmMgvAT2IaLhH0CzEBMqzB7yjYdZqotrkjlWFmAgtaRslD9On4SGFB6NyBcdOQQHvWqQ7XEezM4lzNQyeVr0JvZKMQry-bnZB7j4cmj3J8PCtjFAMUB44Io59eN-jYmcJ5oIkJnOgzPI52hb7aZ5UUa-8eFQTEDP"),
    FoodItem("Sữa tươi", "1.5L", 0.15f, "Hết hạn ngày mai", ErrorColor, ErrorColor, "⚠️", "https://lh3.googleusercontent.com/aida-public/AB6AXuBQc9UaJFJC981KDt7gQFcO0Z50Nq127DTaI3KtF-onQ5345AqfgAukcJ2zOBhlDkc8GNyTFxqCD7OARYj7THI35RyUZS350RK2lmygDRnQSOS_pGm3U4FZtdOYWc4TcKNDJg8w34fWDsEbIRaQNDxBMHe51zf1Z0fRidyodgCWa3vugC5Ei8e3QTCvsZMv5op94pTFE2KKbYcidmhBh7cQ7n-jnc4nESVtZgUww9HZivuIduaXeDrkXZpEVqZPYC3kK11pt8882Xld"),
    FoodItem("Rau cải", "500g", 0.6f, "Còn 4 ngày", PrimaryContainer, Primary, "🌱", "https://lh3.googleusercontent.com/aida-public/AB6AXuDg_9oNJ1GDOiYw1P06Ew_YItk0OExEyuPV5_rhbikUXAu8Qup6wgROrOW4YN_xSOhPhL5MtsGSWJ39JT4FPDsKlxVx-215I1DI_1JiTTCqDRSxjneP-YJ39zFeKZS8No251Z9y7xJL7nLbUGM44iv1lZB_ZhWdp9qLajtSTX_qaQkCsEWPd93we-rKz6ki8YkqvkeWHFMafKMYAHSV_hGD0Fb1goOWxWKftNue-XbbXQNQYNfxkfWUtCyX8mcIiQukiNbqWi-hwE10"),
    FoodItem("Thịt bò", "300g", 0.3f, "Còn 2 ngày", WarningColor, WarningText, "🧊", "https://lh3.googleusercontent.com/aida-public/AB6AXuCanjnhW6P7lbzI0bc7QMWe_JSHpiEGrVUAWMspd3DqC5_Bpbf7ec6_toA7a1h4k1m7uO5cdsV45jbM6aP--aUcMgeIsBzIRqmfbLLjlRAhORk5dSWpboT5Cbhu4T0gEWHCTeIRdgg680tRGvOW7aIwCGy_acUIaGXB4oaHrXqdIf_uBbRNs_-zFeK78UVccYnsdSLWhl7Evaqnoop1FXZzjbDKpYEcoFoBjOJPe8z2MKtcP5svQiRlpO94MkDZl6o2VIWafl91U0bY")
)

@Composable
fun HomeScreen() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            item(span = { GridItemSpan(2) }) {
                VoiceAssistantHeroCard()
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item(span = { GridItemSpan(2) }) {
                CategoriesTabs()
            }
            
            item(span = { GridItemSpan(2) }) {
                SubCategoriesTabs()
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(mockFoodItems) { item ->
                FoodCard(item)
            }
            
            item(span = { GridItemSpan(2) }) {
                Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom nav
            }
        }
}

// TopNavBarGlass moved to Navigation.kt

@Composable
fun VoiceAssistantHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(SurfaceContainerLowest)
            .padding(24.dp)
    ) {
        // Decorative blobs
        Box(
            modifier = Modifier
                .size(128.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .background(PrimaryContainer.copy(alpha = 0.2f), CircleShape)
                .blur(30.dp)
                .align(Alignment.TopStart)
        )
        Box(
            modifier = Modifier
                .size(128.dp)
                .offset(x = 40.dp, y = 40.dp)
                .background(TertiaryContainer.copy(alpha = 0.2f), CircleShape)
                .blur(30.dp)
                .align(Alignment.BottomEnd)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Mic button
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(PrimaryContainer)
                    .shadow(elevation = 4.dp, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🎤", fontSize = 36.sp)
            }

            // Search input
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(Color(0xFFF1F2F6))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text("🔍", color = TextSecondary, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Thử nói \"Nhà còn trứng không?\"", color = TextSecondary, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun CategoriesTabs() {
    val tabs = listOf("Tất cả", "Ngăn mát", "Ngăn đá", "Cánh tủ")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == 0
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isSelected) PrimaryContainer else SurfaceContainerLowest)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = title,
                    color = if (isSelected) OnPrimary else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun SubCategoriesTabs() {
    val tabs = listOf("Rau củ", "Thịt cá", "Trái cây", "Đồ uống", "Gia vị")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        tabs.forEach { title ->
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceContainerLowest)
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun FoodCard(item: FoodItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Freshness Indicator Bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 24.dp)
                    .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                    .background(item.statusColor)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp)
            ) {
                // Image container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, Color.Black.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(item.badgeIcon, fontSize = 12.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextOnSurface)
                Text(item.quantity, color = TextSecondary, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress Bar
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = item.statusColor,
                    trackColor = SurfaceContainer
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(item.statusText, color = item.statusTextColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// BottomNavBarGlass and NavBarItem moved to Navigation.kt

@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}

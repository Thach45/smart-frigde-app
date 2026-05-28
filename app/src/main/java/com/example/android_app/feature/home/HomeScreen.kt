package com.example.android_app.feature.home

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.ui.theme.ErrorColor
import com.example.android_app.ui.theme.OnPrimary
import com.example.android_app.ui.theme.Primary
import com.example.android_app.ui.theme.PrimaryContainer
import com.example.android_app.ui.theme.SurfaceContainer
import com.example.android_app.ui.theme.SurfaceContainerHighest
import com.example.android_app.ui.theme.SurfaceContainerLowest
import com.example.android_app.ui.theme.Tertiary
import com.example.android_app.ui.theme.TertiaryContainer
import com.example.android_app.ui.theme.TextOnSurface
import com.example.android_app.ui.theme.TextSecondary
import com.example.android_app.ui.theme.WarningColor
import com.example.android_app.ui.theme.WarningText


data class FoodItem(
    val id: String,
    val name: String,
    val quantity: String,
    val progress: Float,
    val statusText: String,
    val statusColor: Color,
    val statusTextColor: Color = statusColor,
    val badgeIcon: ImageVector,
    val imageUrl: String
)


@Composable
fun HomeScreen(onFoodClick: (FoodItem) -> Unit = {},
               viewModel: HomeViewModel = hiltViewModel()) {

    val fridgeItems by viewModel.items.collectAsState()
    
    // Tự động load lại data mỗi khi mở lại màn hình này
    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }

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

            items(fridgeItems) { item ->
                FoodCard(item, onClick = { onFoodClick(item) })
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
                Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Primary, modifier = Modifier.size(36.dp))
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
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary, modifier = Modifier.size(24.dp))
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
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) OnPrimary else TextSecondary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun FoodCard(item: FoodItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Image container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(SurfaceContainerHighest)
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
                        .background(Color.White, CircleShape)
                        .padding(4.dp)
                ) {
                    Icon(item.badgeIcon, contentDescription = null, tint = item.statusColor, modifier = Modifier.size(16.dp))
                }
            }
            
            // Text Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )
                Text(
                    item.quantity,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                
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
                
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        item.statusText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = item.statusTextColor
                    )
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
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

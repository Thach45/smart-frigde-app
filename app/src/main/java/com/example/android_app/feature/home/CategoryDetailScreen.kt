package com.example.android_app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.android_app.ui.theme.SurfaceContainer
import com.example.android_app.ui.theme.SurfaceContainerLowest
import com.example.android_app.ui.theme.TextOnSurface
import com.example.android_app.ui.theme.TextSecondary

import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(
    navController: NavController,
    categoryName: String,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val allItems by viewModel.items.collectAsState()
    
    // Tự động load lại data mỗi khi mở lại màn hình này
    LaunchedEffect(Unit) {
        viewModel.loadItems()
    }
    
    val filteredItems = remember(allItems, categoryName) {
        if (categoryName == "Tất cả") return@remember allItems
        
        allItems.filter { item ->
            val n = item.name.lowercase(Locale.getDefault())
            
            val isDoUong = n.contains("sữa") || n.contains("nước") || n.contains("bia") || n.contains("milk") || n.contains("water") || n.contains("drink") || n.contains("juice") || n.contains("pepsi") || n.contains("coca")
            val isThitCa = n.contains("thịt") || n.contains("cá") || n.contains("hồi") || n.contains("bò") || n.contains("heo") || n.contains("gà") || n.contains("tôm") || n.contains("sườn") || n.contains("pork") || n.contains("beef") || n.contains("chicken") || n.contains("fish") || n.contains("trứng") || n.contains("egg")
            val isRauCu = n.contains("rau") || n.contains("cải") || n.contains("xà lách") || n.contains("cà rốt") || n.contains("cà chua") || n.contains("tỏi") || n.contains("ớt") || n.contains("hành") || n.contains("salad") || n.contains("vegetable")
            val isTraiCay = n.contains("táo") || n.contains("chuối") || n.contains("cam") || n.contains("xoài") || n.contains("nho") || n.contains("fruit") || n.contains("dưa") || n.contains("bưởi") || n.contains("ổi")
            val isGiaVi = n.contains("đường") || n.contains("muối") || n.contains("mắm") || n.contains("tiêu") || n.contains("tương") || n.contains("giấm") || n.contains("bột ngọt") || n.contains("hạt nêm")

            when (categoryName) {
                "Đồ uống" -> isDoUong
                "Thịt cá" -> isThitCa
                "Rau củ" -> isRauCu
                "Trái cây" -> isTraiCay
                "Gia vị" -> isGiaVi
                "Khác" -> !isDoUong && !isThitCa && !isRauCu && !isTraiCay && !isGiaVi
                else -> true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = categoryName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA)
                )
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredItems.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("Chưa có đồ ăn trong danh mục này", color = TextSecondary)
                    }
                }
            } else {
                items(filteredItems) { item ->
                    FoodListCard(item, onClick = {
                        navController.navigate("food_detail/${item.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun FoodListCard(item: FoodItem, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(item.statusColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.badgeIcon,
                    contentDescription = null,
                    tint = item.statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Middle section (Name, progress, status text)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    
                    // Quantity Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = item.quantity,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Freshness Progress Bar
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(CircleShape),
                    color = item.statusColor,
                    trackColor = SurfaceContainer
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = item.statusText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = item.statusTextColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Chevron Right
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

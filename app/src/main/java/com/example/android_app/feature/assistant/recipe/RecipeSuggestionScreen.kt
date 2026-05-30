package com.example.android_app.feature.assistant.recipe

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSuggestionScreen(
    navController: NavController,
    targetItemId: String,
    viewModel: RecipeSuggestionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    // Fetch fridge items to compute ingredient availability locally
    var fridgeItems by remember { mutableStateOf<List<FridgeItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        viewModel.loadSuggestion(targetItemId)
        try {
            val result = viewModel.getFridgeItems()
            fridgeItems = result
        } catch (e: Exception) {
            // Fallback empty
        }
    }

    // Show error toast
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Gợi Ý Món Ăn", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        }
    ) { padding ->
        if (uiState.isLoading && uiState.meals.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(color = Primary, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Primary)
                    Text(
                        text = "Gemini đang lên công thức...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Primary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Đang tính toán các nguyên liệu tối ưu trong tủ",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Header Main Ingredient Card
                uiState.fridgeItem?.let { mainIngredient ->
                    MainIngredientHeaderCard(mainIngredient)
                }

                Text(
                    text = "Danh sách Gợi ý Món ăn",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )

                if (uiState.meals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Không tìm thấy gợi ý công thức nào phù hợp.",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    uiState.meals.forEach { meal ->
                        SuggestedMealCard(
                            meal = meal,
                            fridgeItems = fridgeItems,
                            onClick = {
                                navController.navigate("recipe_detail/${meal.id}")
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}



@Composable
fun MainIngredientHeaderCard(item: FridgeItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, SurfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = item.imageUrl ?: "https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=200",
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceContainerHighest)
            )
            Column {
                Text(
                    text = "Nguyên liệu chính",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = item.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )
            }
        }
    }
}

@Composable
fun SuggestedMealCard(
    meal: Meal,
    fridgeItems: List<FridgeItem>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, SurfaceContainer)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recipe image
            AsyncImage(
                model = meal.imageUrl ?: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=200",
                contentDescription = meal.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceContainerHighest)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Title & Save
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = meal.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = "Lưu",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Subtitle: Time & Level
                val level = when (meal.prepTime ?: 15) {
                    in 0..15 -> "Rất dễ"
                    in 16..30 -> "Dễ"
                    else -> "Trung bình"
                }
                Text(
                    text = "${meal.prepTime ?: 15} phút  •  $level",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Text(
                    text = "Nguyên liệu cần thêm:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextOnSurface
                )

                // Horizontal scroll of ingredient chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    meal.ingredients.forEach { ingredient ->
                        val isAvailable = fridgeItems.any {
                            it.name.contains(ingredient.name, ignoreCase = true)
                        }
                        
                        IngredientChip(
                            name = ingredient.name,
                            isAvailable = isAvailable
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IngredientChip(name: String, isAvailable: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isAvailable) SurfaceContainerLowest else Color(0xFFFFEBEE)
            )
            .border(
                1.dp,
                if (isAvailable) SurfaceContainer else Color(0xFFFFCDD2),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = if (isAvailable) "$name (Có sẵn)" else "$name (Thiếu)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isAvailable) TextSecondary else Color(0xFFC62828)
        )
    }
}

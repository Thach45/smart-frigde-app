package com.example.android_app.feature.assistant.recipe

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
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
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.model.RecipeIngredient
import com.example.android_app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    navController: NavController,
    recipeId: String,
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.loadMealDetails(recipeId)
    }

    LaunchedEffect(uiState.isAddedToShopping) {
        if (uiState.isAddedToShopping) {
            Toast.makeText(
                context,
                "Đã thêm các nguyên liệu thiếu của món ${uiState.meal?.title} vào Danh sách đi chợ!",
                Toast.LENGTH_LONG
            ).show()
            viewModel.resetAddedToShopping()
            // Navigate to shopping list tab
            navController.navigate("shopping") {
                popUpTo("home") { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val meal = uiState.meal
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Thông Tin Công Thức", fontWeight = FontWeight.Bold, color = Primary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại", tint = Primary)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "Đang đọc công thức bằng giọng nói...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.VolumeUp, contentDescription = "Đọc công thức", tint = Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Background)
            )
        },
        bottomBar = {
            if (meal != null) {
                Surface(
                    color = SurfaceContainerLowest,
                    shadowElevation = 16.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Button(
                            onClick = { viewModel.addMissingIngredientsToShoppingList() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (uiState.missingIngredients.isEmpty()) Color(0xFF4CAF50) else Primary,
                                contentColor = OnPrimary
                            ),
                            enabled = !uiState.isLoading
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(color = OnPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (uiState.missingIngredients.isEmpty()) "Tất cả có sẵn! Nấu ngay" else "Thêm vào danh sách đi chợ",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading && meal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (meal == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMessage ?: "Không tìm thấy thông tin món ăn",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                // Eco Rescue Alert Banner
                if (uiState.isRescueRecipe) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8F5E9))
                            .border(1.dp, Color(0xFFC8E6C9), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = Color(0xFF388E3C),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Cứu trợ thực phẩm: Dựa trên nguyên liệu sắp hết hạn của bạn.",
                                color = Color(0xFF2E7D32),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Recipe Image with Badge
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .height(240.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    AsyncImage(
                        model = meal.imageUrl ?: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=600",
                        contentDescription = meal.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Freshness Badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2E7D32))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "ĐỘ TƯƠI CAO",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Info Badge
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = meal.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    
                    meal.description?.let {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RecipeInfoBadge(icon = Icons.Default.Timer, text = "${meal.prepTime ?: 15} phút", modifier = Modifier.weight(1f))
                        RecipeInfoBadge(icon = Icons.Default.LocalFireDepartment, text = "${meal.calories ?: 350} kcal", modifier = Modifier.weight(1f))
                        RecipeInfoBadge(icon = Icons.Default.Person, text = "2 người", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Section 1: NGUYÊN LIỆU TỪ TỦ LẠNH
                    if (uiState.availableIngredients.isNotEmpty()) {
                        Text(
                            text = "NGUYÊN LIỆU TỪ TỦ LẠNH",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        uiState.availableIngredients.forEach { (ing, fridgeItem) ->
                            val daysLeft = fridgeItem?.let { getDaysLeft(it.expiryDate) }
                            
                            val expiryText = when {
                                daysLeft == null -> null
                                daysLeft < 0 -> "Đã hết hạn"
                                daysLeft == 0 -> "Hết hạn hôm nay"
                                else -> "Còn $daysLeft ngày"
                            }
                            val expiryColor = when {
                                daysLeft == null -> Color.Transparent
                                daysLeft <= 0 -> Color(0xFFD32F2F)
                                daysLeft <= 2 -> Color(0xFFF57C00)
                                else -> Color(0xFF388E3C)
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = ing.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextOnSurface
                                    )
                                    Text(
                                        text = "Yêu cầu: ${formatQty(ing)}",
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }

                                if (expiryText != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(expiryColor.copy(alpha = 0.12f))
                                            .border(1.dp, expiryColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = expiryText,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = expiryColor
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Section 2: NGUYÊN LIỆU CẦN MUA THÊM
                    if (uiState.missingIngredients.isNotEmpty()) {
                        Text(
                            text = "NGUYÊN LIỆU CẦN MUA THÊM",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        uiState.missingIngredients.forEach { ing ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = ing.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextOnSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = formatQty(ing),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Steps
                    Text(
                        text = "CÁC BƯỚC THỰC HIỆN",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    meal.instructions.forEachIndexed { index, step ->
                        StepItem(step = index + 1, title = "Bước ${index + 1}", description = step)
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

private fun formatQty(ing: RecipeIngredient): String {
    val formattedQty = if (ing.quantity != null) {
        if (ing.quantity % 1.0 == 0.0) ing.quantity.toInt().toString() else ing.quantity.toString()
    } else {
        ""
    }
    return "$formattedQty ${ing.unit ?: ""}".trim()
}

private fun getDaysLeft(dateStr: String?): Int? {
    if (dateStr.isNullOrBlank()) return null
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd"
    )
    for (pattern in patterns) {
        try {
            val format = SimpleDateFormat(pattern, Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = format.parse(dateStr)
            if (date != null) {
                val diff = date.time - System.currentTimeMillis()
                return (diff / (1000 * 60 * 60 * 24)).toInt()
            }
        } catch (e: Exception) {
            // Try next
        }
    }
    return null
}
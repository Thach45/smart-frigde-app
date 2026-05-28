package com.example.android_app.feature.inventory.detail

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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.android_app.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun parseIsoDate(dateStr: String?): Date? {
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
            if (date != null) return date
        } catch (e: Exception) {
            // Tiếp tục thử các pattern khác
        }
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDetailScreen(
    navController: NavController,
    foodId: String,
    viewModel: FoodDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    // Load details
    LaunchedEffect(foodId) {
        viewModel.loadItemDetails(foodId)
    }

    // React to deletion success
    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            Toast.makeText(context, "Đã dùng hết thực phẩm!", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // Show error toasts
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val item = uiState.item
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = Background,
        bottomBar = {
            if (item != null) {
                Surface(
                    color = SurfaceContainerLowest,
                    shadowElevation = 16.dp,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Action 1: Sài hết (Delete)
                        OutlinedButton(
                            onClick = { viewModel.deleteItem(foodId) },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                            border = BorderStroke(1.5.dp, Color(0xFFE53935).copy(alpha = 0.6f)),
                            enabled = !uiState.isLoading
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Sài hết")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sài hết", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        // Action 2: Nấu ăn với món này (Suggest recipe)
                        Button(
                            onClick = { navController.navigate("recipe_suggestion") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Primary,
                                contentColor = OnPrimary
                            ),
                            enabled = !uiState.isLoading
                        ) {
                            Icon(Icons.Default.Restaurant, contentDescription = "Nấu ăn")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Nấu ăn", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (uiState.isLoading && item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.errorMessage ?: "Không tìm thấy thông tin thực phẩm",
                    color = TextSecondary,
                    fontSize = 16.sp
                )
            }
        } else {
            // Parse expiry date robustly
            val expiryDateParsed = parseIsoDate(item.expiryDate)
            val expiryTime = expiryDateParsed?.time ?: System.currentTimeMillis()
            val now = System.currentTimeMillis()
            val daysLeft = ((expiryTime - now) / (1000 * 60 * 60 * 24)).toInt()

            val (statusText, statusColor, progress) = when {
                daysLeft < 0 -> Triple("Đã hết hạn", Color(0xFFBA1A1A), 0f)
                daysLeft <= 2 -> Triple("Sắp hết hạn ($daysLeft ngày)", Color(0xFFF9A825), Math.max(daysLeft / 7f, 0.1f))
                else -> Triple("Còn tươi ($daysLeft ngày)", Color(0xFF4CAF50), Math.min(daysLeft / 7f, 1f))
            }

            val formattedExpiryText = expiryDateParsed?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
            } ?: "Chưa rõ"

            val formattedPriceText = item.price?.let {
                val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                formatter.format(it)
            } ?: "Không có"

            val displayCompartment = when (item.imageUrl) { // Compartment helper logic or direct string map
                // Let's rely on backend compartments: FRIDGE, FREEZER, DOOR, CRISPER
                // Mapping:
                else -> when (item.id.takeLast(1)) { // Fallback, let's write a clean helper
                    else -> "Ngăn mát"
                }
            }
            // Better to display based on some backend field or map it from compartment enum
            // Wait, we don't have compartment directly in domain model FridgeItem!
            // Wait, FridgeItem only has: id, name, quantity, unit, imageUrl, expiryDate, notes, price, kcal.
            // Oh, so compartment isn't in FridgeItem. That's fine, we can show "Tủ lạnh" or default.

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
            ) {
                // Hero Image with Gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    AsyncImage(
                        model = item.imageUrl ?: "https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=600&auto=format&fit=crop",
                        contentDescription = item.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.6f)
                                    )
                                )
                            )
                    )

                    // Navigation Back Button
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = Color.White
                            )
                        }
                    }

                    // Status Badge Float
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .clip(RoundedCornerShape(50))
                            .background(statusColor.copy(alpha = 0.9f))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = statusText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Details Content Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-16).dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                        .background(Background)
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Name and Freshness Slider
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = item.name,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextOnSurface
                        )

                        Text(
                            text = "Độ tươi ngon",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = statusColor,
                            trackColor = SurfaceContainer
                        )
                    }

                    // Primary Attributes Grid Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoCard(
                            icon = Icons.Default.Scale,
                            title = "Số lượng",
                            value = "${item.quantity} ${item.unit ?: ""}".trim(),
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.Default.CalendarToday,
                            title = "Hạn sử dụng",
                            value = formattedExpiryText,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Pricing and Calorie Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoCard(
                            icon = Icons.Default.Sell,
                            title = "Giá mua",
                            value = formattedPriceText,
                            modifier = Modifier.weight(1f)
                        )
                        InfoCard(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "Kcal / Lượng",
                            value = item.kcal?.let { "$it kcal" } ?: "Không rõ",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Ghi chú Section
                    if (!item.notes.isNullOrBlank()) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notes,
                                    contentDescription = "Ghi chú",
                                    tint = Primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Ghi chú bảo quản",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextOnSurface
                                )
                            }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                                border = BorderStroke(1.dp, SurfaceContainer)
                            ) {
                                Text(
                                    text = item.notes,
                                    fontSize = 14.sp,
                                    color = TextOnSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        border = BorderStroke(1.dp, SurfaceContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextOnSurface
            )
        }
    }
}

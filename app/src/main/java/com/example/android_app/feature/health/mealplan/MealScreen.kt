package com.example.android_app.feature.health.mealplan

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

val Primary = Color(0xFF506600)
val OnPrimary = Color(0xFFFFFFFF)
val PrimaryFixedDim = Color(0xFFB1D446)
val Tertiary = Color(0xFF596055)
val TertiaryContainer = Color(0xFFB5BCAE)
val TertiaryFixed = Color(0xFFDEE5D6)
val OnTertiaryFixedVariant = Color(0xFF42493E)
val ErrorColor = Color(0xFFBA1A1A)
val Background = Color(0xFFF9F9F9)
val SurfaceContainerLowest = Color(0xFFFFFFFF)
val SurfaceContainerHighest = Color(0xFFE2E2E2)
val SurfaceContainer = Color(0xFFEEEEEE)
val OutlineVariant = Color(0xFFC5C9B1)
val TextOnSurface = Color(0xFF1A1C1C)
val TextSecondary = Color(0xFF586062)
val ProteinColor = Color(0xFFF97316)
val CarbsColor = Color(0xFF3B82F6)
val FatColor = Color(0xFFEAB308)

@Composable
fun MealScreen(
    navController: NavController,
    viewModel: MealViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMeals()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        HeaderAndWeekView()
        Spacer(modifier = Modifier.height(32.dp))
        NutritionRingsSection()
        Spacer(modifier = Modifier.height(32.dp))

        if (uiState.isLoading && uiState.meals.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            MealPlanSection(
                meals = uiState.meals,
                onMealClick = { mealId ->
                    navController.navigate("recipe_detail/$mealId")
                }
            )
        }

        Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
    }
}

@Composable
fun HeaderAndWeekView() {
    var selectedDate by remember { mutableStateOf("28") }
    
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("Hôm nay", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                Text("Thứ Năm, 28 Tháng 5", fontSize = 16.sp, color = TextSecondary)
            }
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(PrimaryFixedDim.copy(alpha = 0.2f))
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = "Calendar", tint = Primary, modifier = Modifier.size(16.dp).padding(end = 4.dp))
                Text("Lịch", color = Primary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        // Date Scroller
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val dates = listOf(
                Pair("T2", "26"),
                Pair("T3", "27"),
                Pair("T4", "28"),
                Pair("T5", "29"),
                Pair("T6", "30"),
                Pair("T7", "31"),
            )
            
            dates.forEach { date ->
                val isSelected = date.second == selectedDate
                Column(
                    modifier = Modifier
                        .width(56.dp)
                        .height(80.dp)
                        .let {
                            if (isSelected) it.shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)) else it
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Primary else SurfaceContainer)
                        .clickable { selectedDate = date.second },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = date.first, 
                        fontSize = 12.sp, 
                        color = if (isSelected) OnPrimary.copy(alpha = 0.8f) else TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = date.second, 
                        fontSize = 20.sp, 
                        color = if (isSelected) OnPrimary else TextOnSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun NutritionRingsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mục tiêu Dinh dưỡng", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Main Calorie Ring
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceContainerLowest)
                        .border(1.dp, SurfaceContainer, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1450f / 2000f },
                        modifier = Modifier.fillMaxSize(0.8f),
                        color = Primary,
                        trackColor = SurfaceContainerHighest,
                        strokeWidth = 8.dp,
                        strokeCap = StrokeCap.Round
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("1,450", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                        Text("/2000 kcal", fontSize = 12.sp, color = TextSecondary)
                    }
                }
                
                // Macros
                Column(
                    modifier = Modifier.weight(1f).aspectRatio(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    MacroRingRow(label = "Protein", value = "65/120g", color = ProteinColor, progress = 65f/120f, initial = "P")
                    MacroRingRow(label = "Carbs", value = "120/250g", color = CarbsColor, progress = 120f/250f, initial = "C")
                    MacroRingRow(label = "Fat", value = "45/60g", color = FatColor, progress = 45f/60f, initial = "F")
                }
            }
        }
    }
}

@Composable
fun MacroRingRow(label: String, value: String, color: Color, progress: Float, initial: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxSize(),
                color = color,
                trackColor = SurfaceContainerHighest,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round
            )
            Text(initial, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 12.sp, color = TextSecondary)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
        }
    }
}

@Composable
fun MealPlanSection(
    meals: List<com.example.android_app.domain.model.Meal>,
    onMealClick: (String) -> Unit
) {
    Column {
        Text("Lịch trình bữa ăn", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
        Spacer(modifier = Modifier.height(16.dp))
        
        if (meals.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                border = BorderStroke(1.dp, SurfaceContainer)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Chưa có thực đơn nào được chọn",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextOnSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hãy chọn nút 'Nấu ăn' trong chi tiết thực phẩm để nhận gợi ý món ăn từ AI.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            meals.forEach { meal ->
                val formattedCal = meal.calories?.let { "$it kcal" } ?: "350 kcal"
                val dateText = try {
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).apply {
                        timeZone = TimeZone.getTimeZone("UTC")
                    }
                    val parsedDate = format.parse(meal.date)
                    SimpleDateFormat("dd/MM", Locale.getDefault()).format(parsedDate ?: Date())
                } catch (e: Exception) {
                    try {
                        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        val parsedDate = format.parse(meal.date)
                        SimpleDateFormat("dd/MM", Locale.getDefault()).format(parsedDate ?: Date())
                    } catch (e2: Exception) {
                        ""
                    }
                }
                
                val displayDate = if (dateText.isNotBlank()) " • $dateText" else ""

                val statusText = if (meal.status == "ACCEPTED") "Đã chốt" else "Gợi ý"

                MealCard(
                    mealName = "Bữa Ăn$displayDate",
                    statusLabel = "$statusText • $formattedCal",
                    dishName = meal.title,
                    subtitle = meal.description ?: "Tận dụng nguyên liệu từ tủ lạnh",
                    icon = Icons.Default.Restaurant,
                    iconColor = Primary,
                    indicatorColor = PrimaryFixedDim,
                    imageUrl = meal.imageUrl ?: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=200&auto=format&fit=crop",
                    isCompleted = meal.status == "ACCEPTED",
                    isSuggested = false,
                    isWarning = false,
                    onClick = { onMealClick(meal.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MealCard(
    mealName: String,
    statusLabel: String,
    dishName: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    indicatorColor: Color,
    imageUrl: String,
    isCompleted: Boolean,
    isSuggested: Boolean,
    isWarning: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSuggested) 6.dp else 2.dp),
        border = if (isSuggested) BorderStroke(1.dp, Primary.copy(alpha = 0.2f)) else null
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Indicator bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(indicatorColor)
            )
            
            Column(modifier = Modifier.padding(16.dp).weight(1f)) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                        Text(mealName, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = iconColor)
                    }
                    Text(
                        statusLabel, 
                        fontSize = 12.sp, 
                        fontWeight = if (isSuggested) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSuggested) Primary else TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Content
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.let { if (isCompleted) it.alpha(0.6f) else it }
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = dishName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(if (isSuggested) 80.dp else 64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainer)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = dishName, 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = TextOnSurface,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (isSuggested) {
                            Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TertiaryFixed)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Sắp hết hạn: Xà lách", fontSize = 10.sp, color = OnTertiaryFixedVariant)
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(SurfaceContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text("Nấu: 15p", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        } else {
                            Text(
                                text = subtitle, 
                                fontSize = 12.sp, 
                                color = if (isWarning) ErrorColor else TextSecondary
                            )
                        }
                    }
                    
                    if (isCompleted) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Primary, modifier = Modifier.size(28.dp))
                    } else if (isWarning) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { }
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Shop", tint = Primary, modifier = Modifier.size(24.dp))
                        }
                    }
                }
                
                // Suggested Actions
                if (isSuggested) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Nấu món này", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            contentPadding = PaddingValues(0.dp),
                            border = BorderStroke(1.dp, OutlineVariant)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Primary)
                        }
                    }
                }
            }
        }
    }
}
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
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.Calendar
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
val ChartBarColor = Color(0xFF8BC34A)
val ChartBarSelectedColor = Primary

@Composable
fun MealScreen(
    navController: NavController,
    viewModel: MealViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Calculate current week dates
    val calendar = Calendar.getInstance()
    // Find monday of this week
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    
    val weekDates = mutableListOf<Pair<String, String>>()
    val fullDates = mutableListOf<String>()
    
    val displayFormat = SimpleDateFormat("dd", Locale.getDefault())
    val fullFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayNames = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
    
    for (i in 0..6) {
        weekDates.add(Pair(dayNames[i], displayFormat.format(calendar.time)))
        fullDates.add(fullFormat.format(calendar.time))
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }

    // Default to today
    val todayIndex = Calendar.getInstance().let {
        var dayOfWeek = it.get(Calendar.DAY_OF_WEEK) - 2
        if (dayOfWeek < 0) dayOfWeek = 6 // Sunday
        dayOfWeek
    }
    
    var selectedDateIndex by remember { mutableStateOf(todayIndex) }

    LaunchedEffect(Unit) {
        viewModel.loadWeeklyCalories(fullDates[0]) // Load weekly calories starting from Monday
    }

    LaunchedEffect(selectedDateIndex) {
        viewModel.loadMeals(fullDates[selectedDateIndex])
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        HeaderAndWeekView(
            weekDates = weekDates,
            selectedIndex = selectedDateIndex,
            onDateSelected = { selectedDateIndex = it },
            todayText = "Hôm nay, ${weekDates[todayIndex].second} Tháng ${Calendar.getInstance().get(Calendar.MONTH) + 1}"
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        WeeklyCalorieChartSection(uiState.weeklyCalories, selectedDateIndex)
        
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
                },
                onCookClick = { mealId ->
                    viewModel.markAsCooked(mealId, fullDates[selectedDateIndex])
                }
            )
        }

        Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
    }
}

@Composable
fun HeaderAndWeekView(
    weekDates: List<Pair<String, String>>,
    selectedIndex: Int,
    onDateSelected: (Int) -> Unit,
    todayText: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column {
                Text("Lịch trình", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                Text(todayText, fontSize = 16.sp, color = TextSecondary)
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
            weekDates.forEachIndexed { index, date ->
                val isSelected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .width(56.dp)
                        .height(80.dp)
                        .let {
                            if (isSelected) it.shadow(elevation = 8.dp, shape = RoundedCornerShape(12.dp)) else it
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Primary else SurfaceContainer)
                        .clickable { onDateSelected(index) },
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
fun WeeklyCalorieChartSection(weeklyCalories: List<Float>, selectedDateIndex: Int) {
    val maxCal = 2500f
    val days = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")

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
                Text("Biểu đồ Calo 7 ngày", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextOnSurface)
                Icon(Icons.Default.Insights, contentDescription = "Stats", tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bar Chart
            Row(
                modifier = Modifier.fillMaxWidth().height(120.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyCalories.forEachIndexed { index, value ->
                    val isSelected = index == selectedDateIndex
                    val heightRatio = if (value > 0) value / maxCal else 0.05f
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(24.dp)
                                .fillMaxHeight(heightRatio.coerceAtMost(1f))
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (isSelected) ChartBarSelectedColor else ChartBarColor.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = days[index],
                            fontSize = 12.sp,
                            color = if (isSelected) TextOnSurface else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MealPlanSection(
    meals: List<com.example.android_app.domain.model.Meal>,
    onMealClick: (String) -> Unit,
    onCookClick: (String) -> Unit
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
                        text = "Chưa có thực đơn nào",
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
                val isCooked = meal.status == "COOKED"
                val isAccepted = meal.status == "ACCEPTED"
                
                MealCard(
                    mealName = "Bữa Ăn",
                    statusLabel = if (isCooked) "Đã nấu xong" else "Đã chốt • $formattedCal",
                    dishName = meal.title,
                    subtitle = meal.description ?: "Tận dụng nguyên liệu từ tủ lạnh",
                    icon = Icons.Default.Restaurant,
                    iconColor = if (isCooked) TextSecondary else Primary,
                    indicatorColor = if (isCooked) SurfaceContainer else PrimaryFixedDim,
                    imageUrl = meal.imageUrl ?: "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=200&auto=format&fit=crop",
                    isCooked = isCooked,
                    showCookButton = isAccepted,
                    onClick = { onMealClick(meal.id) },
                    onCookClick = { onCookClick(meal.id) }
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
    isCooked: Boolean,
    showCookButton: Boolean,
    onClick: () -> Unit = {},
    onCookClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        color = if (isCooked) TextSecondary else Primary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                
                // Content
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.let { if (isCooked) it.alpha(0.5f) else it }
                ) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = dishName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
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
                            textDecoration = if (isCooked) TextDecoration.LineThrough else null
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = subtitle, 
                            fontSize = 12.sp, 
                            color = TextSecondary,
                            maxLines = 2
                        )
                    }
                    
                    if (isCooked) {
                        Icon(Icons.Default.CheckCircle, contentDescription = "Cooked", tint = Primary, modifier = Modifier.size(28.dp))
                    }
                }
                
                // Suggested Actions
                if (showCookButton) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(
                            onClick = onCookClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary, contentColor = OnPrimary),
                            shape = RoundedCornerShape(50)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Xác nhận đã nấu", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
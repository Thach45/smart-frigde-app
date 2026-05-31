package com.example.android_app.feature.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    viewModel: ShoppingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadShoppingList();
    }
    // Categorize items
    val categorizedItems = remember(uiState.items) {
        val groups = uiState.items.groupBy { item ->
            val name = item.itemName.lowercase()
            when {
                name.contains("thịt") || name.contains("cá") || name.contains("bò") || 
                name.contains("heo") || name.contains("gà") || name.contains("tôm") || 
                name.contains("hải sản") || name.contains("pork") || name.contains("beef") || 
                name.contains("chicken") || name.contains("salmon") -> "Thịt & Hải sản"
                
                name.contains("rau") || name.contains("củ") || name.contains("quả") || 
                name.contains("nấm") || name.contains("cà chua") || name.contains("tỏi") || 
                name.contains("hành") || name.contains("xà lách") || name.contains("salad") ||
                name.contains("bơ") || name.contains("trứng") || name.contains("egg") -> "Rau củ & Trứng"
                
                else -> "Khác"
            }
        }
        groups.toSortedMap()
    }

    Scaffold(
        containerColor = Background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    WasteTrackerWidget(uiState.wasteStats)
                }

                item {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Primary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "AI Gợi Ý Đi Chợ",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextOnSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tự động cập nhật các nguyên liệu còn thiếu từ thực đơn của bạn.",
                            fontSize = 14.sp,
                            color = TextSecondary
                        )
                    }
                }

                if (uiState.items.isEmpty() && !uiState.isLoading) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Danh sách trống. Hãy lên kế hoạch thực đơn hoặc thêm món thủ công!",
                                    color = TextSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    categorizedItems.forEach { (categoryName, items) ->
                        item {
                            Text(
                                text = categoryName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary,
                                modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                            )
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    items.forEachIndexed { index, item ->
                                        ShoppingItemRow(
                                            item = item,
                                            onToggle = {
                                                viewModel.toggleShoppingItem(item.id, !item.isPurchased)
                                            },
                                            onDelete = {
                                                viewModel.deleteShoppingItem(item.id)
                                            }
                                        )
                                        if (index < items.size - 1) {
                                            HorizontalDivider(color = SurfaceContainer, modifier = Modifier.padding(horizontal = 16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimaryContainer
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Thêm món khác", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom nav
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var quantityStr by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Thêm nguyên liệu cần mua", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Tên nguyên liệu") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = quantityStr,
                            onValueChange = { quantityStr = it },
                            label = { Text("Số lượng") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Đơn vị (VD: g, cái...)") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            val quantity = quantityStr.toDoubleOrNull()
                            viewModel.addShoppingItem(name, quantity, unit.takeIf { it.isNotBlank() })
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Text("Thêm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Hủy", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun WasteTrackerWidget(stats: com.example.android_app.data.remote.api.WasteStatsResponse?) {
    val thisWeekWeight = stats?.thisWeekWeight ?: 0f
    val percentChange = stats?.percentChange ?: 0
    val displayPercent = if (percentChange >= 0) "+$percentChange%" else "$percentChange%"
    val badgeColor = if (percentChange <= 0) Primary else ErrorColor

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("LÃNG PHÍ TUẦN QUA", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${thisWeekWeight} kg", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(badgeColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(displayPercent, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = badgeColor)
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Eco, contentDescription = "Eco", tint = Primary, modifier = Modifier.size(32.dp))
            }
        }
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = if (item.isPurchased) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = "Check",
            tint = if (item.isPurchased) Primary else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(28.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.itemName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (item.isPurchased) TextSecondary else TextOnSurface,
                textDecoration = if (item.isPurchased) TextDecoration.LineThrough else null
            )
            item.quantity?.let { qty ->
                val formattedQty = if (qty % 1.0 == 0.0) qty.toInt().toString() else qty.toString()
                Text(
                    text = "$formattedQty ${item.unit ?: ""}",
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = ErrorColor.copy(alpha = 0.7f),
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

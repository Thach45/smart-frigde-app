package com.example.android_app.ui.sceens.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_app.ui.theme.*

// Mock data model
data class ShoppingItem(
    val id: Int,
    val name: String,
    val note: String,
    val quantity: String,
    var isChecked: Boolean = false
)

data class ShoppingCategory(
    val name: String,
    val items: List<ShoppingItem>
)

@Composable
fun ShoppingScreen() {
    val categories = remember {
        mutableStateListOf(
            ShoppingCategory(
                "Rau củ quả",
                listOf(
                    ShoppingItem(1, "Cà chua bi", "Salad cho Thứ 3", "500g", false),
                    ShoppingItem(2, "Xà lách Romaine", "Sắp tới hạn tủ lạnh", "2 bắp", false)
                )
            ),
            ShoppingCategory(
                "Thịt cá",
                listOf(
                    ShoppingItem(3, "Cá hồi phi lê", "Cho món Thứ 4", "300g", false),
                    ShoppingItem(4, "Thịt bò băm", "Cho món Thứ 5", "400g", true)
                )
            )
        )
    }

    Scaffold(
        containerColor = Background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                WasteTrackerWidget()
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
                        text = "Dựa trên thực đơn tuần tới và tủ lạnh hiện tại.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                }
            }

            categories.forEachIndexed { catIndex, category ->
                item {
                    Text(
                        text = category.name,
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
                            category.items.forEachIndexed { itemIndex, item ->
                                ShoppingItemRow(item = item, onToggle = {
                                    val updatedItems = category.items.toMutableList()
                                    updatedItems[itemIndex] = item.copy(isChecked = !item.isChecked)
                                    categories[catIndex] = category.copy(items = updatedItems)
                                })
                                if (itemIndex < category.items.size - 1) {
                                    HorizontalDivider(color = SurfaceContainer, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { /* TODO */ },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
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
    }
}

@Composable
fun WasteTrackerWidget() {
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
                    Text("1.2 kg", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextOnSurface)
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PrimaryContainer.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("-15%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
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
fun ShoppingItemRow(item: ShoppingItem, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(16.dp)
    ) {
        Icon(
            imageVector = if (item.isChecked) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = "Check",
            tint = if (item.isChecked) Primary else TextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(28.dp)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (item.isChecked) TextSecondary else TextOnSurface,
                textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
            )
            Text(
                text = item.note,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
        
        Text(
            text = item.quantity,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (item.isChecked) TextSecondary else TextOnSurface
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShoppingScreenPreview() {
    ShoppingScreen()
}

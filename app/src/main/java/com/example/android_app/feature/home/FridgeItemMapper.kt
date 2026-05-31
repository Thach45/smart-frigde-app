package com.example.android_app.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import com.example.android_app.domain.model.FridgeItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

class FridgeItemMapper @Inject constructor() {

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
                // Tiếp tục thử pattern khác
            }
        }
        return null
    }

    private fun getCategoryIcon(name: String): ImageVector {
        val n = name.lowercase(Locale.getDefault())
        return when {
            n.contains("sữa") || n.contains("nước") || n.contains("bia") || n.contains("nước ngọt") || n.contains("milk") || n.contains("water") || n.contains("drink") || n.contains("juice") || n.contains("pepsi") || n.contains("coca") -> {
                Icons.Default.LocalDrink
            }
            n.contains("thịt") || n.contains("cá") || n.contains("hồi") || n.contains("bò") || n.contains("heo") || n.contains("gà") || n.contains("tôm") || n.contains("sườn") || n.contains("pork") || n.contains("beef") || n.contains("chicken") || n.contains("fish") || n.contains("trứng") || n.contains("egg") -> {
                Icons.Default.Restaurant
            }
            n.contains("rau") || n.contains("cải") || n.contains("xà lách") || n.contains("cà rốt") || n.contains("cà chua") || n.contains("tỏi") || n.contains("ớt") || n.contains("hành") || n.contains("táo") || n.contains("chuối") || n.contains("cam") || n.contains("xoài") || n.contains("nho") || n.contains("salad") || n.contains("fruit") || n.contains("vegetable") -> {
                Icons.Default.Spa
            }
            else -> {
                Icons.Default.Kitchen
            }
        }
    }

    fun mapToUiModel(item: FridgeItem): FoodItem {
        val expiryTime = parseIsoDate(item.expiryDate)?.time ?: System.currentTimeMillis()

        val now = System.currentTimeMillis()
        val daysLeft = ((expiryTime - now) / (1000 * 60 * 60 * 24)).toInt()

        val (statusText, color) = when {
            daysLeft < 0 -> Pair("Đã hỏng", Color(0xFFBA1A1A))
            daysLeft <= 2 -> Pair("Sắp hỏng", Color(0xFFF9A825))
            else -> Pair("Còn tươi", Color(0xFF4CAF50))
        }

        val formattedQuantity = if (item.quantity % 1.0 == 0.0) {
            item.quantity.toInt().toString()
        } else {
            item.quantity.toString()
        }

        return FoodItem(
            id = item.id,
            name = item.name,
            quantity = "$formattedQuantity ${item.unit ?: ""}".trim(),
            progress = if (daysLeft > 0) Math.min(daysLeft / 7f, 1f) else 0f,
            statusText = statusText,
            statusColor = color,
            badgeIcon = getCategoryIcon(item.name),
            imageUrl = item.imageUrl ?: "https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=200&auto=format&fit=crop"
        )
    }

    fun mapListToUiModel(items: List<FridgeItem>): List<FoodItem> {
        return items.map { mapToUiModel(it) }
    }
}

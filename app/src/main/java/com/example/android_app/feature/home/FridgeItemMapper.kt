package com.example.android_app.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
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

    fun mapToUiModel(item: FridgeItem): FoodItem {
        val expiryTime = parseIsoDate(item.expiryDate)?.time ?: System.currentTimeMillis()

        val now = System.currentTimeMillis()
        val daysLeft = ((expiryTime - now) / (1000 * 60 * 60 * 24)).toInt()

        val (statusText, color, icon) = when {
            daysLeft < 0 -> Triple("Đã hỏng", Color(0xFFBA1A1A), Icons.Default.Error)
            daysLeft <= 2 -> Triple("Sắp hỏng", Color(0xFFF9A825), Icons.Default.Warning)
            else -> Triple("Còn tươi", Color(0xFF4CAF50), Icons.Default.CheckCircle)
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
            badgeIcon = icon,
            imageUrl = item.imageUrl ?: "https://images.unsplash.com/photo-1596484552834-6a58f850e0a1?q=80&w=200&auto=format&fit=crop"
        )
    }

    fun mapListToUiModel(items: List<FridgeItem>): List<FoodItem> {
        return items.map { mapToUiModel(it) }
    }
}

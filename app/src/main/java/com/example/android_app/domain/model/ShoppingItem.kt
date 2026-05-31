package com.example.android_app.domain.model

data class ShoppingItem(
    val id: String,
    val userId: String,
    val itemName: String,
    val quantity: Double?,
    val unit: String?,
    val isPurchased: Boolean,
    val mealId: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

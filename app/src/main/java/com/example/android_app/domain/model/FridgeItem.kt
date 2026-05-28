package com.example.android_app.domain.model

data class FridgeItem(
    val id: String,
    val name: String,
    val quantity: Double,
    val unit: String? = null,
    val imageUrl: String? = null,
    val expiryDate: String? = null,
    val notes: String? = null,
    val price: Double? = null,
    val kcal: Int? = null
)

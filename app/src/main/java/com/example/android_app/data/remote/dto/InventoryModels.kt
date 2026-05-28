package com.example.android_app.data.remote.dto

data class AddInventoryRequest(
    val name: String,
    val quantity: Double,
    val unit: String,
    val category: String,
    val compartment: String,
    val imageUrl: String?,
    val expiryDate: String,
    val notes: String?,
    val price: Double?,
    val kcal: Int?
)

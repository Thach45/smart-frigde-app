package com.example.android_app.domain.model

data class ShoppingItem(
    val id: String,
    val name: String,
    val note: String,
    val quantity: String,
    val isChecked: Boolean = false,
    val categoryName: String
)

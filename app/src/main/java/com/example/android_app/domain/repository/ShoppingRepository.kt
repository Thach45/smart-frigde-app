package com.example.android_app.domain.repository

import com.example.android_app.domain.model.ShoppingItem

interface ShoppingRepository {
    suspend fun getShoppingList(): Result<List<ShoppingItem>>
    suspend fun addShoppingItem(itemName: String, quantity: Double?, unit: String?, mealId: String? = null): Result<ShoppingItem>
    suspend fun toggleShoppingItem(id: String, isPurchased: Boolean): Result<ShoppingItem>
    suspend fun deleteShoppingItem(id: String): Result<Unit>
}

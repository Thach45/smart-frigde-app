package com.example.android_app.domain.repository

import com.example.android_app.domain.model.ShoppingItem
import kotlinx.coroutines.flow.StateFlow

interface ShoppingRepository {
    val shoppingItems: StateFlow<List<ShoppingItem>>
    suspend fun getShoppingItems(): Result<List<ShoppingItem>>
    suspend fun toggleShoppingItem(id: String): Result<Unit>
    suspend fun addShoppingItem(name: String, note: String, quantity: String, categoryName: String): Result<ShoppingItem>
    suspend fun addMissingIngredients(mealTitle: String, missingIngredients: List<Pair<String, String>>): Result<Unit>
}

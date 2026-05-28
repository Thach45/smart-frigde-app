package com.example.android_app.domain.repository

import com.example.android_app.domain.model.FridgeItem

interface InventoryRepository {
    suspend fun addManualFood(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        compartment: String,
        imageUrl: String?,
        expiryDate: String,
        notes: String?,
        price: Double?,
        kcal: Int?
    ): Result<FridgeItem>

    suspend fun getInventoryItems(): Result<List<FridgeItem>>

    suspend fun getItemDetails(id: String): Result<FridgeItem>

    suspend fun deleteItem(id: String): Result<Unit>
}

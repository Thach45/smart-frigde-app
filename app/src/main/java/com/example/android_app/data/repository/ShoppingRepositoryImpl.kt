package com.example.android_app.data.repository

import com.example.android_app.data.remote.api.ShoppingApiService
import com.example.android_app.data.remote.api.AddShoppingItemRequest
import com.example.android_app.data.remote.api.ToggleShoppingItemRequest
import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.domain.repository.ShoppingRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingRepositoryImpl @Inject constructor(
    private val shoppingApiService: ShoppingApiService
) : ShoppingRepository {

    override suspend fun getShoppingList(): Result<List<ShoppingItem>> {
        return try {
            val response = shoppingApiService.getShoppingList()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addShoppingItem(
        itemName: String,
        quantity: Double?,
        unit: String?,
        mealId: String?
    ): Result<ShoppingItem> {
        return try {
            val response = shoppingApiService.addShoppingItem(AddShoppingItemRequest(itemName, quantity, unit, mealId))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body response from server"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleShoppingItem(id: String, isPurchased: Boolean): Result<ShoppingItem> {
        return try {
            val response = shoppingApiService.toggleShoppingItem(id, ToggleShoppingItemRequest(isPurchased))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body response from server"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteShoppingItem(id: String): Result<Unit> {
        return try {
            val response = shoppingApiService.deleteShoppingItem(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

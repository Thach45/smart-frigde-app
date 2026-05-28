package com.example.android_app.data.repository

import com.example.android_app.data.remote.api.InventoryApiService
import com.example.android_app.data.remote.dto.AddInventoryRequest
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val apiService: InventoryApiService
) : InventoryRepository {
    override suspend fun addManualFood(
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
    ): Result<FridgeItem> {
        return try {
            val response = apiService.addManualFood(
                AddInventoryRequest(
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    category = category,
                    compartment = compartment,
                    imageUrl = imageUrl,
                    expiryDate = expiryDate,
                    notes = notes,
                    price = price,
                    kcal = kcal
                )
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getInventoryItems(): Result<List<FridgeItem>> {
        return try {
            val response = apiService.getInventoryItems()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Lỗi lấy dữ liệu: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getItemDetails(id: String): Result<FridgeItem> {
        return try {
            val response = apiService.getItemDetails(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Lỗi lấy chi tiết thực phẩm: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteItem(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteItem(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Lỗi xóa thực phẩm: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

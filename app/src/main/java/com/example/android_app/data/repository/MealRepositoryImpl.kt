package com.example.android_app.data.repository

import com.example.android_app.data.remote.api.MealApiService
import com.example.android_app.data.remote.dto.SuggestFromItemRequest
import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.repository.MealRepository
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealApiService: MealApiService
) : MealRepository {

    override suspend fun suggestFromItem(targetItemId: String): Result<Meal> {
        return try {
            val response = mealApiService.suggestFromItem(SuggestFromItemRequest(targetItemId))
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

    override suspend fun acceptMeal(mealId: String): Result<Meal> {
        return try {
            val response = mealApiService.acceptMeal(mealId)
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

    override suspend fun getMeals(): Result<List<Meal>> {
        return try {
            val response = mealApiService.getMeals()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Lỗi lấy thực đơn: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMealDetails(mealId: String): Result<Meal> {
        return try {
            val response = mealApiService.getMealDetails(mealId)
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
}

package com.example.android_app.data.repository

import com.example.android_app.data.remote.api.MealApiService
import com.example.android_app.data.remote.dto.SuggestFromItemRequest
import com.example.android_app.domain.model.DailyCalorie
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

    override suspend fun getMeals(date: String?): Result<List<Meal>> {
        return try {
            val response = mealApiService.getMeals(date)
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
    override suspend fun markMealCooked(mealId: String): Result<Meal> {
        return try {
            val response = mealApiService.markMealAsCooked(mealId)
            if (response.isSuccessful) {
                val meal = response.body() ?: Meal(
                    id = mealId, 
                    title = "", 
                    status = "COOKED",
                    userId = "",
                    date = "",
                    mealType = "",
                    description = null,
                    ingredients = emptyList(),
                    instructions = emptyList()
                )
                Result.success(meal)
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getWeeklyCalories(startDate: String): Result<List<DailyCalorie>> {
        return try {
            val response = mealApiService.getWeeklyCalories(startDate)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendVoiceCommand(text: String): Result<com.example.android_app.data.remote.dto.VoiceResponse> {
        return try {
            val response = mealApiService.sendVoiceCommand(com.example.android_app.data.remote.dto.VoiceRequest(text))
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
}

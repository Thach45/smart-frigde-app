package com.example.android_app.domain.repository

import com.example.android_app.domain.model.DailyCalorie
import com.example.android_app.domain.model.Meal

interface MealRepository {
    suspend fun suggestFromItem(targetItemId: String): Result<Meal>
    suspend fun acceptMeal(mealId: String): Result<Meal>
    suspend fun getMeals(date: String? = null): Result<List<Meal>>
    suspend fun markMealCooked(mealId: String): Result<Meal>
    suspend fun getMealDetails(mealId: String): Result<Meal>
    suspend fun getWeeklyCalories(startDate: String): Result<List<DailyCalorie>>
    suspend fun sendVoiceCommand(text: String): Result<com.example.android_app.data.remote.dto.VoiceResponse>
}

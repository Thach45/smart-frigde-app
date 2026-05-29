package com.example.android_app.domain.repository

import com.example.android_app.domain.model.Meal

interface MealRepository {
    suspend fun suggestFromItem(targetItemId: String): Result<Meal>
    suspend fun acceptMeal(mealId: String): Result<Meal>
    suspend fun getMeals(): Result<List<Meal>>
    suspend fun getMealDetails(mealId: String): Result<Meal>
}

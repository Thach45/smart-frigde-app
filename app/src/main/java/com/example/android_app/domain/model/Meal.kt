package com.example.android_app.domain.model

data class Meal(
    val id: String,
    val userId: String,
    val date: String,
    val mealType: String,
    val status: String,
    val title: String,
    val description: String?,
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String>,
    val imageUrl: String? = null,
    val prepTime: Int? = null,
    val calories: Int? = null
)

data class RecipeIngredient(
    val name: String,
    val quantity: Double? = null,
    val unit: String? = null
)

data class DailyCalorie(
    val date: String,
    val totalCalories: Int
)

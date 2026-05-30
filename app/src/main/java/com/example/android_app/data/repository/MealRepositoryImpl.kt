package com.example.android_app.data.repository

import com.example.android_app.data.remote.api.MealApiService
import com.example.android_app.data.remote.dto.SuggestFromItemRequest
import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.model.RecipeIngredient
import com.example.android_app.domain.repository.MealRepository
import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MealRepositoryImpl @Inject constructor(
    private val mealApiService: MealApiService,
    private val inventoryRepository: InventoryRepository
) : MealRepository {

    private val mockMeals = listOf(
        Meal(
            id = "1",
            userId = "test-user",
            date = "2026-05-29",
            mealType = "Gợi ý",
            status = "Gợi ý",
            title = "Bánh Pudding sữa",
            description = "Món tráng miệng thanh mát, dễ làm tại nhà.",
            ingredients = listOf(
                RecipeIngredient("Sữa tươi", 200.0, "ml"),
                RecipeIngredient("Đường", 50.0, "g"),
                RecipeIngredient("Gelatin", 10.0, "g"),
                RecipeIngredient("Vani", 1.0, "ống")
            ),
            instructions = listOf("Ngâm gelatin vào nước lạnh.", "Đun ấm sữa tươi với đường.", "Hòa gelatin vào sữa ấm.", "Rót ra khuôn và để lạnh."),
            imageUrl = "https://images.unsplash.com/photo-1541832676-9b763b0239ab?auto=format&fit=crop&q=80&w=200",
            prepTime = 30,
            calories = 180
        ),
        Meal(
            id = "2",
            userId = "test-user",
            date = "2026-05-29",
            mealType = "Gợi ý",
            status = "Gợi ý",
            title = "Sinh tố bơ sữa",
            description = "Sinh tố béo ngậy bổ dưỡng cho ngày hè.",
            ingredients = listOf(
                RecipeIngredient("Sữa tươi", 100.0, "ml"),
                RecipeIngredient("Bơ", 1.0, "quả"),
                RecipeIngredient("Sữa đặc", 2.0, "muỗng")
            ),
            instructions = listOf("Lột vỏ bơ, cắt nhỏ.", "Cho bơ, sữa tươi, sữa đặc và đá bào vào máy xay.", "Xay nhuyễn mịn.", "Rót ra ly và thưởng thức."),
            imageUrl = "https://images.unsplash.com/photo-1553530666-ba11a7da3888?auto=format&fit=crop&q=80&w=200",
            prepTime = 10,
            calories = 220
        ),
        Meal(
            id = "3",
            userId = "test-user",
            date = "2026-05-29",
            mealType = "Gợi ý",
            status = "Gợi ý",
            title = "Salad Ức Gà Thanh Mát",
            description = "Salad ức gà giảm cân giàu dinh dưỡng.",
            ingredients = listOf(
                RecipeIngredient("Ức gà", 200.0, "g"),
                RecipeIngredient("Xà lách lolo", 100.0, "g"),
                RecipeIngredient("Cà chua bi", 50.0, "g"),
                RecipeIngredient("Sốt mè rang", 2.0, "muỗng")
            ),
            instructions = listOf("Luộc chín ức gà, xé nhỏ.", "Rửa sạch rau xà lách, cà chua bi cắt đôi.", "Trộn rau, cà chua và ức gà vào tô.", "Rưới sốt mè rang lên và trộn đều."),
            imageUrl = "https://images.unsplash.com/photo-1540420773420-3366772f4999?auto=format&fit=crop&q=80&w=200",
            prepTime = 15,
            calories = 320
        )
    )

    override suspend fun suggestFromItem(targetItemId: String): Result<List<Meal>> {
        return try {
            val response = mealApiService.suggestFromItem(SuggestFromItemRequest(targetItemId))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(listOf(it))
                } ?: Result.success(getFallbackSuggestions(targetItemId))
            } else {
                Result.success(getFallbackSuggestions(targetItemId))
            }
        } catch (e: Exception) {
            Result.success(getFallbackSuggestions(targetItemId))
        }
    }

    private suspend fun getFallbackSuggestions(targetItemId: String): List<Meal> {
        val itemNameResult = inventoryRepository.getItemDetails(targetItemId)
        val itemName = if (itemNameResult.isSuccess) {
            itemNameResult.getOrNull()?.name ?: ""
        } else {
            // Hardcode fallback mappings for common IDs in mock data
            when (targetItemId) {
                "2" -> "Sữa tươi"
                "5" -> "Ức gà"
                else -> ""
            }
        }

        if (itemName.isBlank()) return mockMeals

        return mockMeals.filter { meal ->
            meal.ingredients.any { ingredient ->
                ingredient.name.contains(itemName, ignoreCase = true)
            }
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
                val mockMeal = mockMeals.firstOrNull { it.id == mealId }
                if (mockMeal != null) Result.success(mockMeal) else Result.failure(Exception("Meal not found"))
            }
        } catch (e: Exception) {
            val mockMeal = mockMeals.firstOrNull { it.id == mealId }
            if (mockMeal != null) Result.success(mockMeal) else Result.failure(e)
        }
    }

    override suspend fun getMeals(): Result<List<Meal>> {
        return try {
            val response = mealApiService.getMeals()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.success(mockMeals)
            }
        } catch (e: Exception) {
            Result.success(mockMeals)
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
                val mockMeal = mockMeals.firstOrNull { it.id == mealId }
                if (mockMeal != null) Result.success(mockMeal) else Result.failure(Exception("Meal not found"))
            }
        } catch (e: Exception) {
            val mockMeal = mockMeals.firstOrNull { it.id == mealId }
            if (mockMeal != null) Result.success(mockMeal) else Result.failure(e)
        }
    }
}

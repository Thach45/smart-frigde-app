package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.repository.MealRepository
import javax.inject.Inject

class GetMealDetailsUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(mealId: String): Result<Meal> {
        if (mealId.isBlank()) {
            return Result.failure(Exception("ID món ăn không được để trống"))
        }
        return mealRepository.getMealDetails(mealId)
    }
}

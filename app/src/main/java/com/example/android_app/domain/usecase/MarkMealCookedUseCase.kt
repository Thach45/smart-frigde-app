package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.repository.MealRepository
import javax.inject.Inject

class MarkMealCookedUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(mealId: String): Result<Meal> {
        return mealRepository.markMealCooked(mealId)
    }
}

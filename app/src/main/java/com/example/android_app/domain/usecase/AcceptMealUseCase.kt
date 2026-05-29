package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.repository.MealRepository
import javax.inject.Inject

class AcceptMealUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(mealId: String): Result<Meal> {
        if (mealId.isBlank()) {
            return Result.failure(Exception("ID thực đơn không được để trống"))
        }
        return mealRepository.acceptMeal(mealId)
    }
}

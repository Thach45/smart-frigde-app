package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.repository.MealRepository
import javax.inject.Inject

class GetMealsUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(): Result<List<Meal>> {
        return mealRepository.getMeals()
    }
}

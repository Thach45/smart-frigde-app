package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.DailyCalorie
import com.example.android_app.domain.repository.MealRepository
import javax.inject.Inject

class GetWeeklyCaloriesUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(startDate: String): Result<List<DailyCalorie>> {
        return mealRepository.getWeeklyCalories(startDate)
    }
}

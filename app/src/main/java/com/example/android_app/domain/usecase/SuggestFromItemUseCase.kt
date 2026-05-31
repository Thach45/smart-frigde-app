package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.repository.MealRepository
import javax.inject.Inject

class SuggestFromItemUseCase @Inject constructor(
    private val mealRepository: MealRepository
) {
    suspend operator fun invoke(targetItemId: String): Result<List<Meal>> {
        if (targetItemId.isBlank()) {
            return Result.failure(Exception("ID nguyên liệu không được để trống"))
        }
        return mealRepository.suggestFromItem(targetItemId)
    }
}

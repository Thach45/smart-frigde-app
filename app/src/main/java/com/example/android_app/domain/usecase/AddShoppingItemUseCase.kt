package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.domain.repository.ShoppingRepository
import javax.inject.Inject

class AddShoppingItemUseCase @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) {
    suspend operator fun invoke(itemName: String, quantity: Double?, unit: String?, mealId: String? = null): Result<ShoppingItem> {
        if (itemName.isBlank()) {
            return Result.failure(Exception("Tên nguyên liệu không được để trống"))
        }
        return shoppingRepository.addShoppingItem(itemName, quantity, unit, mealId)
    }
}

package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.domain.repository.ShoppingRepository
import javax.inject.Inject

class ToggleShoppingItemUseCase @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) {
    suspend operator fun invoke(id: String, isPurchased: Boolean): Result<ShoppingItem> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID không được để trống"))
        }
        return shoppingRepository.toggleShoppingItem(id, isPurchased)
    }
}

package com.example.android_app.domain.usecase

import com.example.android_app.domain.repository.ShoppingRepository
import javax.inject.Inject

class DeleteShoppingItemUseCase @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID không được để trống"))
        }
        return shoppingRepository.deleteShoppingItem(id)
    }
}

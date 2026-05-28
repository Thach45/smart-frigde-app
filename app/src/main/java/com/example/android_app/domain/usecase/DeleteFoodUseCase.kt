package com.example.android_app.domain.usecase

import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject

class DeleteFoodUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID không được để trống"))
        }
        return inventoryRepository.deleteItem(id)
    }
}

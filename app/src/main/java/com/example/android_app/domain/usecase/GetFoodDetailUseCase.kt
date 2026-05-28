package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject

class GetFoodDetailUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(id: String): Result<FridgeItem> {
        if (id.isBlank()) {
            return Result.failure(Exception("ID không được để trống"))
        }
        return inventoryRepository.getItemDetails(id)
    }
}

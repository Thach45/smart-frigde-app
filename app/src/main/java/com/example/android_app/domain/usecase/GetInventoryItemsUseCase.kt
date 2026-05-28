package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject

class GetInventoryItemsUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(): Result<List<FridgeItem>> {
        // Tương lai bạn có thể thêm logic phân loại, sắp xếp hạn sử dụng ở đây
        return inventoryRepository.getInventoryItems()
    }
}
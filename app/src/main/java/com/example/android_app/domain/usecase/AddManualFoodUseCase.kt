package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject

class AddManualFoodUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        compartment: String,
        imageUrl: String?,
        expiryDate: String,
        notes: String?,
        price: Double?,
        kcal: Int?
    ): Result<FridgeItem> {
        // Basic validation
        if (name.isBlank()) {
            return Result.failure(Exception("Tên không được để trống"))
        }
        if (quantity <= 0.0) {
            return Result.failure(Exception("Số lượng phải lớn hơn 0"))
        }
        return inventoryRepository.addManualFood(
            name = name,
            quantity = quantity,
            unit = unit,
            category = category,
            compartment = compartment,
            imageUrl = imageUrl,
            expiryDate = expiryDate,
            notes = notes,
            price = price,
            kcal = kcal
        )
    }
}

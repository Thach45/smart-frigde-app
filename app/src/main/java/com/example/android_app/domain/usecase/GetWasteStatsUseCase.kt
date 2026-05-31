package com.example.android_app.domain.usecase

import com.example.android_app.data.remote.api.WasteStatsResponse
import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject

class GetWasteStatsUseCase @Inject constructor(
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(): Result<WasteStatsResponse> {
        return inventoryRepository.getWasteStats()
    }
}

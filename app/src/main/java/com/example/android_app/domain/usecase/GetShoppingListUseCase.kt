package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.domain.repository.ShoppingRepository
import javax.inject.Inject

class GetShoppingListUseCase @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) {
    suspend operator fun invoke(): Result<List<ShoppingItem>> {
        return shoppingRepository.getShoppingList()
    }
}

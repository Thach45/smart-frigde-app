package com.example.android_app.data.repository

import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShoppingRepositoryImpl @Inject constructor() : ShoppingRepository {

    private val _shoppingItems = MutableStateFlow<List<ShoppingItem>>(
        listOf(
            ShoppingItem("1", "Cà chua bi", "Cho Salad Tự Chọn (Thứ 3)", "500g", false, "Rau củ quả"),
            ShoppingItem("2", "Xà lách Romaine", "Sắp hết trong tủ lạnh", "2 bắp", false, "Rau củ quả"),
            ShoppingItem("3", "Cá hồi phi lê", "Cho Bữa Tối (Thứ 4)", "300g", false, "Thịt cá"),
            ShoppingItem("4", "Thịt bò băm", "Cho Mì Ý (Thứ 5)", "400g", true, "Thịt cá")
        )
    )
    
    override val shoppingItems: StateFlow<List<ShoppingItem>> = _shoppingItems.asStateFlow()

    override suspend fun getShoppingItems(): Result<List<ShoppingItem>> {
        return Result.success(_shoppingItems.value)
    }

    override suspend fun toggleShoppingItem(id: String): Result<Unit> {
        _shoppingItems.value = _shoppingItems.value.map {
            if (it.id == id) it.copy(isChecked = !it.isChecked) else it
        }
        return Result.success(Unit)
    }

    override suspend fun addShoppingItem(
        name: String,
        note: String,
        quantity: String,
        categoryName: String
    ): Result<ShoppingItem> {
        val newItem = ShoppingItem(
            id = UUID.randomUUID().toString(),
            name = name,
            note = note,
            quantity = quantity,
            isChecked = false,
            categoryName = categoryName
        )
        _shoppingItems.value = _shoppingItems.value + newItem
        return Result.success(newItem)
    }

    override suspend fun addMissingIngredients(
        mealTitle: String,
        missingIngredients: List<Pair<String, String>>
    ): Result<Unit> {
        val current = _shoppingItems.value.toMutableList()
        missingIngredients.forEach { (name, qty) ->
            // Check if item with same name already exists
            val exists = current.any { it.name.equals(name, ignoreCase = true) }
            if (!exists) {
                current.add(
                    ShoppingItem(
                        id = UUID.randomUUID().toString(),
                        name = name,
                        note = "Cho món $mealTitle",
                        quantity = qty,
                        isChecked = false,
                        categoryName = getCategoryForIngredient(name)
                    )
                )
            }
        }
        _shoppingItems.value = current
        return Result.success(Unit)
    }

    private fun getCategoryForIngredient(name: String): String {
        val vegetables = listOf("cà chua", "xà lách", "rau", "hành", "tỏi", "bơ", "nấm", "dưa leo", "gelatin", "vani", "đường")
        val meatAndFish = listOf("thịt", "cá", "tôm", "gà", "bò", "heo", "hải sản")
        val normalized = name.lowercase()
        return when {
            vegetables.any { normalized.contains(it) } -> "Rau củ quả"
            meatAndFish.any { normalized.contains(it) } -> "Thịt cá"
            else -> "Khác"
        }
    }
}

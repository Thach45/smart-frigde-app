package com.example.android_app.feature.assistant.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.model.RecipeIngredient
import com.example.android_app.domain.repository.InventoryRepository
import com.example.android_app.domain.repository.ShoppingRepository
import com.example.android_app.domain.usecase.GetMealDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getMealDetailsUseCase: GetMealDetailsUseCase,
    private val inventoryRepository: InventoryRepository,
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadMealDetails(mealId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState(isLoading = true)
            val result = getMealDetailsUseCase(mealId)
            if (result.isSuccess) {
                val meal = result.getOrNull()
                if (meal != null) {
                    // Fetch fridge items to compute ingredient status
                    val fridgeResult = inventoryRepository.getInventoryItems()
                    val fridgeItems = fridgeResult.getOrNull() ?: emptyList()

                    val available = mutableListOf<Pair<RecipeIngredient, FridgeItem?>>()
                    val missing = mutableListOf<RecipeIngredient>()
                    var isRescue = false

                    meal.ingredients.forEach { ingredient ->
                        val matchingFridgeItem = fridgeItems.firstOrNull {
                            it.name.contains(ingredient.name, ignoreCase = true)
                        }
                        if (matchingFridgeItem != null) {
                            available.add(ingredient to matchingFridgeItem)
                            // Check if item is close to expiring (<= 2 days left)
                            val daysLeft = getDaysLeft(matchingFridgeItem.expiryDate)
                            if (daysLeft != null && daysLeft <= 2) {
                                isRescue = true
                            }
                        } else {
                            missing.add(ingredient)
                        }
                    }

                    _uiState.value = RecipeDetailUiState(
                        isLoading = false,
                        meal = meal,
                        availableIngredients = available,
                        missingIngredients = missing,
                        isRescueRecipe = isRescue
                    )
                } else {
                    _uiState.value = RecipeDetailUiState(
                        isLoading = false,
                        errorMessage = "Không tìm thấy thông tin món ăn"
                    )
                }
            } else {
                _uiState.value = RecipeDetailUiState(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tải chi tiết món ăn"
                )
            }
        }
    }

    fun addMissingIngredientsToShoppingList() {
        if (_uiState.value.isLoading || _uiState.value.isAddedToShopping) return
        viewModelScope.launch {
            val meal = _uiState.value.meal ?: return@launch
            val missing = _uiState.value.missingIngredients
            if (missing.isEmpty()) {
                _uiState.value = _uiState.value.copy(isAddedToShopping = true)
                return@launch
            }

            _uiState.value = _uiState.value.copy(isLoading = true)
            val missingPairs = missing.map {
                val formattedQty = if (it.quantity != null) {
                    if (it.quantity % 1.0 == 0.0) it.quantity.toInt().toString() else it.quantity.toString()
                } else {
                    ""
                }
                it.name to "$formattedQty ${it.unit ?: ""}".trim()
            }

            val result = shoppingRepository.addMissingIngredients(meal.title, missingPairs)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAddedToShopping = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi thêm nguyên liệu vào danh sách đi chợ"
                )
            }
        }
    }

    fun resetAddedToShopping() {
        _uiState.value = _uiState.value.copy(isAddedToShopping = false)
    }

    private fun getDaysLeft(dateStr: String?): Int? {
        if (dateStr.isNullOrBlank()) return null
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss",
            "yyyy-MM-dd"
        )
        for (pattern in patterns) {
            try {
                val format = SimpleDateFormat(pattern, Locale.getDefault()).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val date = format.parse(dateStr)
                if (date != null) {
                    val diff = date.time - System.currentTimeMillis()
                    return (diff / (1000 * 60 * 60 * 24)).toInt()
                }
            } catch (e: Exception) {
                // Try next pattern
            }
        }
        return null
    }
}

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val meal: Meal? = null,
    val availableIngredients: List<Pair<RecipeIngredient, FridgeItem?>> = emptyList(),
    val missingIngredients: List<RecipeIngredient> = emptyList(),
    val isRescueRecipe: Boolean = false,
    val isAddedToShopping: Boolean = false,
    val errorMessage: String? = null
)

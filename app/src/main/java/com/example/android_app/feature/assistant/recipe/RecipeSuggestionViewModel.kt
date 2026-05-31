package com.example.android_app.feature.assistant.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.repository.InventoryRepository
import com.example.android_app.domain.usecase.AcceptMealUseCase
import com.example.android_app.domain.usecase.SuggestFromItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeSuggestionViewModel @Inject constructor(
    private val suggestFromItemUseCase: SuggestFromItemUseCase,
    private val acceptMealUseCase: AcceptMealUseCase,
    private val inventoryRepository: InventoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeSuggestionUiState())
    val uiState: StateFlow<RecipeSuggestionUiState> = _uiState.asStateFlow()

    fun loadSuggestion(targetItemId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeSuggestionUiState(isLoading = true)
            
            // Get ingredient detail for header
            val foodResult = inventoryRepository.getItemDetails(targetItemId)
            val fridgeItem = foodResult.getOrNull()

            val result = suggestFromItemUseCase(targetItemId)
            if (result.isSuccess) {
                _uiState.value = RecipeSuggestionUiState(
                    isLoading = false,
                    meals = result.getOrNull() ?: emptyList(),
                    fridgeItem = fridgeItem
                )
            } else {
                _uiState.value = RecipeSuggestionUiState(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tạo gợi ý món ăn từ AI",
                    fridgeItem = fridgeItem
                )
            }
        }
    }

    fun acceptMeal(mealId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = acceptMealUseCase(mealId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAccepted = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi chốt nấu món ăn"
                )
            }
        }
    }

    suspend fun getFridgeItems(): List<FridgeItem> {
        return inventoryRepository.getInventoryItems().getOrNull() ?: emptyList()
    }
}

data class RecipeSuggestionUiState(
    val isLoading: Boolean = false,
    val meals: List<Meal> = emptyList(),
    val fridgeItem: FridgeItem? = null,
    val isAccepted: Boolean = false,
    val errorMessage: String? = null
)

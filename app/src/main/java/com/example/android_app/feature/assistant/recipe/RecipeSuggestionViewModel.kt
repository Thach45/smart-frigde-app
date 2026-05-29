package com.example.android_app.feature.assistant.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.Meal
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
    private val acceptMealUseCase: AcceptMealUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeSuggestionUiState())
    val uiState: StateFlow<RecipeSuggestionUiState> = _uiState.asStateFlow()

    fun loadSuggestion(targetItemId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeSuggestionUiState(isLoading = true)
            val result = suggestFromItemUseCase(targetItemId)
            if (result.isSuccess) {
                _uiState.value = RecipeSuggestionUiState(
                    isLoading = false,
                    meal = result.getOrNull()
                )
            } else {
                _uiState.value = RecipeSuggestionUiState(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tạo gợi ý món ăn từ AI"
                )
            }
        }
    }

    fun acceptMeal(mealId: String) {
        viewModelScope.launch {
            val currentMeal = _uiState.value.meal ?: return@launch
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
}

data class RecipeSuggestionUiState(
    val isLoading: Boolean = false,
    val meal: Meal? = null,
    val isAccepted: Boolean = false,
    val errorMessage: String? = null
)

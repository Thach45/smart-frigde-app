package com.example.android_app.feature.assistant.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.usecase.GetMealDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val getMealDetailsUseCase: GetMealDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    fun loadMealDetails(mealId: String) {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState(isLoading = true)
            val result = getMealDetailsUseCase(mealId)
            if (result.isSuccess) {
                _uiState.value = RecipeDetailUiState(
                    isLoading = false,
                    meal = result.getOrNull()
                )
            } else {
                _uiState.value = RecipeDetailUiState(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tải chi tiết món ăn"
                )
            }
        }
    }
}

data class RecipeDetailUiState(
    val isLoading: Boolean = false,
    val meal: Meal? = null,
    val errorMessage: String? = null
)

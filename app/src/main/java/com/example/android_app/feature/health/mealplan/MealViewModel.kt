package com.example.android_app.feature.health.mealplan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.Meal
import com.example.android_app.domain.usecase.GetMealsUseCase
import com.example.android_app.domain.usecase.GetWeeklyCaloriesUseCase
import com.example.android_app.domain.usecase.MarkMealCookedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    private val getMealsUseCase: GetMealsUseCase,
    private val markMealCookedUseCase: MarkMealCookedUseCase,
    private val getWeeklyCaloriesUseCase: GetWeeklyCaloriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MealUiState())
    val uiState: StateFlow<MealUiState> = _uiState.asStateFlow()

    fun loadMeals(date: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = getMealsUseCase(date)
            if (result.isSuccess) {
                var meals = result.getOrNull() ?: emptyList()
                
                // Fallback: If backend returns all meals, filter them locally
                if (date != null) {
                    meals = meals.filter { it.date.startsWith(date) }
                }
                
                val totalCalories = meals.sumOf { it.calories ?: 0 }
                val completedMeals = meals.count { it.status == "COOKED" }
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    meals = meals,
                    totalCalories = totalCalories,
                    completedMeals = completedMeals,
                    totalPlannedMeals = meals.size
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi lấy danh sách thực đơn"
                )
            }
        }
    }

    fun markAsCooked(mealId: String, currentDate: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = markMealCookedUseCase(mealId)
            if (result.isSuccess) {
                // Reload meals after marking as cooked
                loadMeals(currentDate)

            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi xác nhận nấu"
                )
            }
        }
    }
    
    fun loadWeeklyCalories(startDate: String) {
        viewModelScope.launch {
            val result = getWeeklyCaloriesUseCase(startDate)
            if (result.isSuccess) {
                val dailyData = result.getOrNull() ?: emptyList()
                
                // Map the results to a 7-element Float array based on date offset from startDate
                val floatArray = FloatArray(7) { 0f }
                try {
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val start = format.parse(startDate)
                    if (start != null) {
                        for (data in dailyData) {
                            val currentDate = format.parse(data.date)
                            if (currentDate != null) {
                                val diff = (currentDate.time - start.time) / (1000 * 60 * 60 * 24)
                                if (diff in 0..6) {
                                    floatArray[diff.toInt()] = data.totalCalories.toFloat()
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore parse errors
                }
                
                _uiState.value = _uiState.value.copy(
                    weeklyCalories = floatArray.toList()
                )
            }
        }
    }
}

data class MealUiState(
    val isLoading: Boolean = false,
    val meals: List<Meal> = emptyList(),
    val totalCalories: Int = 0,
    val completedMeals: Int = 0,
    val totalPlannedMeals: Int = 0,
    val weeklyCalories: List<Float> = List(7) { 0f },
    val errorMessage: String? = null
)
package com.example.android_app.feature.inventory.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.usecase.AddManualFoodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class AddManualViewModel @Inject constructor(
    private val addManualFoodUseCase: AddManualFoodUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddManualUiState())
    val uiState: StateFlow<AddManualUiState> = _uiState.asStateFlow()

    fun addFood(
        name: String,
        quantityStr: String,
        unit: String,
        category: String,
        compartment: String,
        expiryTimestamp: Long?,
        imageUrl: String,
        notes: String,
        priceStr: String,
        kcalStr: String
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, isSuccess = false)
            
            val quantity = quantityStr.trim().toDoubleOrNull() ?: 0.0
            val price = priceStr.trim().toDoubleOrNull()
            val kcal = kcalStr.trim().toIntOrNull()
            
            // Map compartment
            val mappedCompartment = when (compartment) {
                "Ngăn đá" -> "FREEZER"
                "Cánh tủ" -> "DOOR"
                else -> "FRIDGE"
            }
            
            // Calculate ISO-8601 expiry date from selected timestamp or default to 3 days
            val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            val expiryDate = if (expiryTimestamp != null) {
                format.format(Date(expiryTimestamp))
            } else {
                val calendar = Calendar.getInstance()
                calendar.add(Calendar.DAY_OF_YEAR, 3)
                format.format(calendar.time)
            }
            
            val result = addManualFoodUseCase(
                name = name,
                quantity = quantity,
                unit = unit,
                category = category,
                compartment = mappedCompartment,
                imageUrl = imageUrl.ifBlank { null },
                expiryDate = expiryDate,
                notes = notes.ifBlank { null },
                price = price,
                kcal = kcal
            )
            
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true)
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Thêm thực phẩm thất bại"
                )
            }
        }
    }
}

data class AddManualUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

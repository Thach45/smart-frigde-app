package com.example.android_app.feature.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.domain.usecase.GetShoppingListUseCase
import com.example.android_app.domain.usecase.AddShoppingItemUseCase
import com.example.android_app.domain.usecase.ToggleShoppingItemUseCase
import com.example.android_app.domain.usecase.DeleteShoppingItemUseCase
import com.example.android_app.domain.usecase.GetWasteStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val getShoppingListUseCase: GetShoppingListUseCase,
    private val addShoppingItemUseCase: AddShoppingItemUseCase,
    private val toggleShoppingItemUseCase: ToggleShoppingItemUseCase,
    private val deleteShoppingItemUseCase: DeleteShoppingItemUseCase,
    private val getWasteStatsUseCase: GetWasteStatsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingUiState())
    val uiState: StateFlow<ShoppingUiState> = _uiState.asStateFlow()

    init {
        loadShoppingList()
    }

    fun loadShoppingList() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            // Load waste stats asynchronously or sequentially
            val wasteResult = getWasteStatsUseCase()
            val stats = wasteResult.getOrNull()

            val result = getShoppingListUseCase()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    items = result.getOrNull() ?: emptyList(),
                    wasteStats = stats
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi tải danh sách đi chợ",
                    wasteStats = stats
                )
            }
        }
    }

    fun addShoppingItem(itemName: String, quantity: Double?, unit: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = addShoppingItemUseCase(itemName, quantity, unit)
            if (result.isSuccess) {
                loadShoppingList()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi thêm món đi chợ"
                )
            }
        }
    }

    fun toggleShoppingItem(itemId: String, isPurchased: Boolean) {
        viewModelScope.launch {
            val result = toggleShoppingItemUseCase(itemId, isPurchased)
            if (result.isSuccess) {
                // Update local list directly for fast UI responsiveness
                val updatedItems = _uiState.value.items.map {
                    if (it.id == itemId) it.copy(isPurchased = isPurchased) else it
                }
                _uiState.value = _uiState.value.copy(items = updatedItems)
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi cập nhật trạng thái món"
                )
            }
        }
    }

    fun deleteShoppingItem(itemId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = deleteShoppingItemUseCase(itemId)
            if (result.isSuccess) {
                loadShoppingList()
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "Lỗi xóa món đi chợ"
                )
            }
        }
    }
}

data class ShoppingUiState(
    val isLoading: Boolean = false,
    val items: List<ShoppingItem> = emptyList(),
    val wasteStats: com.example.android_app.data.remote.api.WasteStatsResponse? = null,
    val errorMessage: String? = null
)

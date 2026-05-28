package com.example.android_app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.usecase.GetInventoryItemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getInventoryItemsUseCase: GetInventoryItemsUseCase,
    private val mapper: FridgeItemMapper
) : ViewModel() {
    private val _items = MutableStateFlow<List<FoodItem>>(emptyList())
    val items = _items.asStateFlow()
    
    init {
        loadItems() // Tự động load dữ liệu khi mở màn hình
    }

    fun loadItems() {
        viewModelScope.launch {
            val result = getInventoryItemsUseCase()
            if (result.isSuccess) {
                val rawItems = result.getOrNull() ?: emptyList()
                _items.value = mapper.mapListToUiModel(rawItems)
            } else {
                // Xử lý lỗi (Hiện Toast hoặc Snackbar)
            }
        }
    }
}
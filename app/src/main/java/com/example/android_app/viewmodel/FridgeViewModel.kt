package com.example.android_app.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_app.model.FridgeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FridgeViewModel : ViewModel() {
    private val _items = MutableStateFlow<List<FridgeItem>>(emptyList())
    val items: StateFlow<List<FridgeItem>> = _items.asStateFlow()

    init {
        // Mock data
        _items.value = listOf(
            FridgeItem(1, "Milk", 2),
            FridgeItem(2, "Eggs", 12),
            FridgeItem(3, "Apple", 5)
        )
    }

    fun addItem(name: String, quantity: Int) {
        val newItem = FridgeItem(
            id = (_items.value.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            quantity = quantity
        )
        _items.value = _items.value + newItem
    }
}

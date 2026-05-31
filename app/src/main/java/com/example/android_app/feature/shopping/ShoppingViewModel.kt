package com.example.android_app.feature.shopping

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.model.ShoppingItem
import com.example.android_app.domain.repository.ShoppingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository
) : ViewModel() {

    val shoppingItems: StateFlow<List<ShoppingItem>> = shoppingRepository.shoppingItems

    fun toggleItem(id: String) {
        viewModelScope.launch {
            shoppingRepository.toggleShoppingItem(id)
        }
    }

    fun addItem(name: String, note: String, quantity: String, categoryName: String) {
        viewModelScope.launch {
            shoppingRepository.addShoppingItem(name, note, quantity, categoryName)
        }
    }
}

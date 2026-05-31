package com.example.android_app.feature.assistant.voice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.domain.repository.MealRepository
import com.example.android_app.domain.model.FridgeItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VoiceUiState(
    val reply: String = "",
    val success: Boolean = false,
    val ingredient: FridgeItem? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class VoiceViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoiceUiState())
    val uiState: StateFlow<VoiceUiState> = _uiState

    fun sendTextToBackend(text: String) {
        _uiState.value = VoiceUiState(isLoading = true)
        viewModelScope.launch {
            val result = mealRepository.sendVoiceCommand(text)
            if (result.isSuccess) {
                val data = result.getOrNull()
                _uiState.value = VoiceUiState(
                    reply = data?.message ?: "Thành công",
                    success = data?.success ?: false,
                    ingredient = data?.ingredient
                )
            } else {
                _uiState.value = VoiceUiState(
                    error = result.exceptionOrNull()?.message ?: "Lỗi gửi giọng nói lên máy chủ"
                )
            }
        }
    }
}
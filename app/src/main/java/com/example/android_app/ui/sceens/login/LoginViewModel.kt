package com.example.android_app.ui.sceens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_app.data.api.AuthApiService
import com.example.android_app.data.local.TokenStore
import com.example.android_app.data.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val userName: String) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApiService: AuthApiService,
    private val tokenStore: TokenStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = LoginUiState.Error("Vui lòng nhập email và mật khẩu")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = authApiService.login(LoginRequest(email.trim(), password))
                if (response.isSuccessful) {
                    val body = response.body()!!
                    // Lưu token vào bộ nhớ cục bộ
                    tokenStore.accessToken = body.accessToken
                    tokenStore.refreshToken = body.refreshToken
                    tokenStore.userId = body.user.id
                    tokenStore.userName = body.user.name
                    _uiState.value = LoginUiState.Success(body.user.name)
                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Email hoặc mật khẩu không đúng"
                        400 -> "Dữ liệu không hợp lệ"
                        500 -> "Lỗi server, thử lại sau"
                        else -> "Đăng nhập thất bại (${response.code()})"
                    }
                    _uiState.value = LoginUiState.Error(errorMsg)
                }
            } catch (e: java.net.ConnectException) {
                _uiState.value = LoginUiState.Error("Không kết nối được server. Kiểm tra backend đã chạy chưa?")
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Lỗi: ${e.localizedMessage}")
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}

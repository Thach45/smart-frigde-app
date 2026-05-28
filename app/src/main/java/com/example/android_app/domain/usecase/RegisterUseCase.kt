package com.example.android_app.domain.usecase

import com.example.android_app.data.local.TokenStore
import com.example.android_app.data.remote.dto.AuthResponse
import com.example.android_app.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenStore: TokenStore
) {
    suspend operator fun invoke(email: String, password: String, name: String): Result<AuthResponse> {
        val result = authRepository.register(email, password, name)
        if (result.isSuccess) {
            val response = result.getOrNull()
            if (response != null) {
                tokenStore.accessToken = response.accessToken
                tokenStore.refreshToken = response.refreshToken
                tokenStore.userId = response.user.id
                tokenStore.userName = response.user.name
            }
        }
        return result
    }
}

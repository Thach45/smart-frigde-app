package com.example.android_app.domain.repository

import com.example.android_app.data.remote.dto.AuthResponse

interface AuthRepository {
    suspend fun register(email: String, password: String, name: String): Result<AuthResponse>
}

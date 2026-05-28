package com.example.android_app.data.remote.api

import com.example.android_app.data.remote.dto.LoginRequest
import com.example.android_app.data.remote.dto.RegisterRequest
import com.example.android_app.data.remote.dto.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
}

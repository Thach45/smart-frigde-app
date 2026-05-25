package com.example.android_app.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)

data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    @SerializedName("createdAt") val createdAt: String? = null
)

data class ApiError(
    val error: String,
    val code: String? = null
)

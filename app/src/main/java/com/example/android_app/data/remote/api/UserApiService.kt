package com.example.android_app.data.remote.api

import com.example.android_app.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

data class UserResponse(
    val user: User
)

data class UpdateProfileRequest(
    val name: String? = null,
    val healthGoal: String? = null
)

interface UserApiService {
    @GET("/auth/me")
    suspend fun getProfile(): Response<UserResponse>

    @PATCH("/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserResponse>
}

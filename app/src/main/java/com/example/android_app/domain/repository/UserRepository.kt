package com.example.android_app.domain.repository

import com.example.android_app.domain.model.User

interface UserRepository {
    suspend fun getProfile(): Result<User>
    suspend fun updateProfile(name: String?, healthGoal: String?): Result<User>
}

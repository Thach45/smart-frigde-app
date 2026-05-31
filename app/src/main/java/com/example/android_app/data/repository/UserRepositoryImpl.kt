package com.example.android_app.data.repository

import com.example.android_app.data.remote.api.UserApiService
import com.example.android_app.data.remote.api.UpdateProfileRequest
import com.example.android_app.domain.model.User
import com.example.android_app.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
) : UserRepository {

    override suspend fun getProfile(): Result<User> {
        return try {
            val response = userApiService.getProfile()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.user)
                } ?: Result.failure(Exception("Empty body response from server"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(name: String?, healthGoal: String?): Result<User> {
        return try {
            val response = userApiService.updateProfile(UpdateProfileRequest(name, healthGoal))
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it.user)
                } ?: Result.failure(Exception("Empty body response from server"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

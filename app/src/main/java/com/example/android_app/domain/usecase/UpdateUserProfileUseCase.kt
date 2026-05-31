package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.User
import com.example.android_app.domain.repository.UserRepository
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(name: String?, healthGoal: String?): Result<User> {
        return userRepository.updateProfile(name, healthGoal)
    }
}

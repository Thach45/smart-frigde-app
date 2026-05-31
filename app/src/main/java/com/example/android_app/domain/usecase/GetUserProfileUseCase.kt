package com.example.android_app.domain.usecase

import com.example.android_app.domain.model.User
import com.example.android_app.domain.repository.UserRepository
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> {
        return userRepository.getProfile()
    }
}

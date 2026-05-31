package com.example.android_app.di

import com.example.android_app.data.repository.AuthRepositoryImpl
import com.example.android_app.data.repository.InventoryRepositoryImpl
import com.example.android_app.domain.repository.AuthRepository
import com.example.android_app.domain.repository.InventoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindInventoryRepository(
        inventoryRepositoryImpl: InventoryRepositoryImpl
    ): InventoryRepository

    @Binds
    @Singleton
    abstract fun bindMealRepository(
        mealRepositoryImpl: com.example.android_app.data.repository.MealRepositoryImpl
    ): com.example.android_app.domain.repository.MealRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: com.example.android_app.data.repository.UserRepositoryImpl
    ): com.example.android_app.domain.repository.UserRepository

    @Binds
    @Singleton
    abstract fun bindShoppingRepository(
        shoppingRepositoryImpl: com.example.android_app.data.repository.ShoppingRepositoryImpl
    ): com.example.android_app.domain.repository.ShoppingRepository
}

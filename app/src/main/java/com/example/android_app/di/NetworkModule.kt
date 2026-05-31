package com.example.android_app.di

import com.example.android_app.data.local.TokenStore
import com.example.android_app.data.remote.api.AuthApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Máy ảo Android dùng 10.0.2.2 để trỏ về localhost của máy tính
    // Thiết bị thật cần đổi sang IP LAN của máy tính (VD: http://192.168.1.x:4000)
    private const val BASE_URL = "http://10.0.2.2:4000"

    @Provides
    @Singleton
    fun provideOkHttpClient(tokenStore: TokenStore): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                tokenStore.accessToken?.let { token ->
                    request.addHeader("Authorization", "Bearer $token")
                }
                chain.proceed(request.build())
            }
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryApiService(retrofit: Retrofit): com.example.android_app.data.remote.api.InventoryApiService {
        return retrofit.create(com.example.android_app.data.remote.api.InventoryApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMealApiService(retrofit: Retrofit): com.example.android_app.data.remote.api.MealApiService {
        return retrofit.create(com.example.android_app.data.remote.api.MealApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): com.example.android_app.data.remote.api.UserApiService {
        return retrofit.create(com.example.android_app.data.remote.api.UserApiService::class.java)
    }
}

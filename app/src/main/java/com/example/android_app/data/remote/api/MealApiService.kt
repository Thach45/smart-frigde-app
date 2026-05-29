package com.example.android_app.data.remote.api

import com.example.android_app.data.remote.dto.SuggestFromItemRequest
import com.example.android_app.domain.model.Meal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MealApiService {
    @POST("/meals/suggest-from-item")
    suspend fun suggestFromItem(@Body request: SuggestFromItemRequest): Response<Meal>

    @POST("/meals/{id}/accept")
    suspend fun acceptMeal(@Path("id") id: String): Response<Meal>

    @GET("/meals")
    suspend fun getMeals(): Response<List<Meal>>

    @GET("/meals/{id}")
    suspend fun getMealDetails(@Path("id") id: String): Response<Meal>
}

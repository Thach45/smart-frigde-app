package com.example.android_app.data.remote.api

import com.example.android_app.data.remote.dto.SuggestFromItemRequest
import com.example.android_app.domain.model.DailyCalorie
import com.example.android_app.domain.model.Meal
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MealApiService {
    @POST("/meals/suggest-from-item")
    suspend fun suggestFromItem(@Body request: SuggestFromItemRequest): Response<Meal>

    @POST("/meals/{id}/accept")
    suspend fun acceptMeal(@Path("id") id: String): Response<Meal>

    @GET("/meals")
    suspend fun getMeals(@Query("date") date: String? = null): Response<List<Meal>>

    @PUT("/meals/{id}/cook")
    suspend fun markMealAsCooked(@Path("id") id: String): Response<Meal>

    @GET("/meals/{id}")
    suspend fun getMealDetails(@Path("id") id: String): Response<Meal>

    @GET("/meals/calories/weekly")
    suspend fun getWeeklyCalories(@Query("startDate") startDate: String): Response<List<DailyCalorie>>

    @POST("/meals/voice-assistant")
    suspend fun sendVoiceCommand(@Body request: com.example.android_app.data.remote.dto.VoiceRequest): Response<com.example.android_app.data.remote.dto.VoiceResponse>
}

package com.example.android_app.data.remote.api

import com.example.android_app.domain.model.ShoppingItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

data class AddShoppingItemRequest(
    val itemName: String,
    val quantity: Double? = null,
    val unit: String? = null,
    val mealId: String? = null
)

data class ToggleShoppingItemRequest(
    val isPurchased: Boolean
)

interface ShoppingApiService {
    @GET("/shopping-list")
    suspend fun getShoppingList(): Response<List<ShoppingItem>>

    @POST("/shopping-list")
    suspend fun addShoppingItem(@Body request: AddShoppingItemRequest): Response<ShoppingItem>

    @PATCH("/shopping-list/{id}")
    suspend fun toggleShoppingItem(@Path("id") id: String, @Body request: ToggleShoppingItemRequest): Response<ShoppingItem>

    @DELETE("/shopping-list/{id}")
    suspend fun deleteShoppingItem(@Path("id") id: String): Response<Unit>
}

package com.example.android_app.data.remote.api

import com.example.android_app.data.remote.dto.AddInventoryRequest
import com.example.android_app.domain.model.FridgeItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class WasteStatsResponse(
    val thisWeekWeight: Float,
    val lastWeekWeight: Float,
    val percentChange: Int
)

interface InventoryApiService {
    @GET("/inventory")
    suspend fun getInventoryItems(): Response<List<FridgeItem>>

    @POST("/inventory")
    suspend fun addManualFood(@Body request: AddInventoryRequest): Response<FridgeItem>

    @GET("/inventory/waste/stats")
    suspend fun getWasteStats(): Response<WasteStatsResponse>

    @GET("/inventory/{id}")
    suspend fun getItemDetails(@Path("id") id: String): Response<FridgeItem>

    @DELETE("/inventory/{id}")
    suspend fun deleteItem(@Path("id") id: String): Response<Unit>
}

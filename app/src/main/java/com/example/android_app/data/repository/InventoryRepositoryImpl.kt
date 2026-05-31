package com.example.android_app.data.repository

import com.example.android_app.data.remote.api.InventoryApiService
import com.example.android_app.data.remote.dto.AddInventoryRequest
import com.example.android_app.domain.model.FridgeItem
import com.example.android_app.domain.repository.InventoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InventoryRepositoryImpl @Inject constructor(
    private val apiService: InventoryApiService
) : InventoryRepository {

    private val mockItems = listOf(
        FridgeItem(
            id = "1",
            name = "Trứng gà",
            quantity = 10.0,
            unit = "quả",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBQlZA8DVr0kdjLBmZGu6hUU4KqDXZ13s2TwhfuCmiERqmoWd_myf1xNDUh7GA-HYFcZqmH8xihMiplUWcJ-DhJkPalcooTk8v3D7RR_Gxzm1t1jmMgvAT2IaLhH0CzEBMqzB7yjYdZqotrkjlWFmAgtaRslD9On4SGFB6NyBcdOQQHvWqQ7XEezM4lzNQyeVr0JvZKMQry-bnZB7j4cmj3J8PCtjFAMUB44Io59eN-jYmcJ5oIkJnOgzPI52hb7aZ5UUa-8eFQTEDP",
            expiryDate = "2026-06-10",
            notes = "Khay trứng",
            price = 30000.0,
            kcal = 155
        ),
        FridgeItem(
            id = "2",
            name = "Sữa tươi",
            quantity = 1.5,
            unit = "L",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBQc9UaJFJC981KDt7gQFcO0Z50Nq127DTaI3KtF-onQ5345AqfgAukcJ2zOBhlDkc8GNyTFxqCD7OARYj7THI35RyUZS350RK2lmygDRnQSOS_pGm3U4FZtdOYWc4TcKNDJg8w34fWDsEbIRaQNDxBMHe51zf1Z0fRidyodgCWa3vugC5Ei8e3QTCvsZMv5op94pTFE2KKbYcidmhBh7cQ7n-jnc4nESVtZgUww9HZivuIduaXeDrkXZpEVqZPYC3kK11pt8882Xld",
            expiryDate = "2023-10-25",
            notes = "Ngăn mát/Cánh tủ",
            price = 45000.0,
            kcal = 120
        ),
        FridgeItem(
            id = "3",
            name = "Rau cải",
            quantity = 500.0,
            unit = "g",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDg_9oNJ1GDOiYw1P06Ew_YItk0OExEyuPV5_rhbikUXAu8Qup6wgROrOW4YN_xSOhPhL5MtsGSWJ39JT4FPDsKlxVx-215I1DI_1JiTTCqDRSxjneP-YJ39zFeKZS8No251Z9y7xJL7nLbUGM44iv1lZB_ZhWdp9qLajtSTX_qaQkCsEWPd93we-rKz6ki8YkqvkeWHFMafKMYAHSV_hGD0Fb1goOWxWKftNue-XbbXQNQYNfxkfWUtCyX8mcIiQukiNbqWi-hwE10",
            expiryDate = "2026-06-02",
            notes = "Hộc rau",
            price = 15000.0,
            kcal = 25
        ),
        FridgeItem(
            id = "4",
            name = "Thịt bò",
            quantity = 300.0,
            unit = "g",
            imageUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuCanjnhW6P7lbzI0bc7QMWe_JSHpiEGrVUAWMspd3DqC5_Bpbf7ec6_toA7a1h4k1m7uO5cdsV45jbM6aP--aUcMgeIsBzIRqmfbLLjlRAhORk5dSWpboT5Cbhu4T0gEWHCTeIRdgg680tRGvOW7aIwCGy_acUIaGXB4oaHrXqdIf_uBbRNs_-zFeK78UVccYnsdSLWhl7Evaqnoop1FXZzjbDKpYEcoFoBjOJPe8z2MKtcP5svQiRlpO94MkDZl6o2VIWafl91U0bY",
            expiryDate = "2026-05-31",
            notes = "Kệ trên",
            price = 90000.0,
            kcal = 250
        ),
        FridgeItem(
            id = "5",
            name = "Ức gà",
            quantity = 500.0,
            unit = "g",
            imageUrl = "https://images.unsplash.com/photo-1604503468506-a8da13d82791?auto=format&fit=crop&q=80&w=200",
            expiryDate = "2026-05-29",
            notes = "Kệ giữa",
            price = 40000.0,
            kcal = 165
        ),
        FridgeItem(
            id = "6",
            name = "Xà lách lolo",
            quantity = 200.0,
            unit = "g",
            imageUrl = "https://images.unsplash.com/photo-1556801712-76c8eb07bbc9?auto=format&fit=crop&q=80&w=200",
            expiryDate = "2026-05-31",
            notes = "Hộc rau",
            price = 12000.0,
            kcal = 15
        ),
        FridgeItem(
            id = "7",
            name = "Cà chua bi",
            quantity = 300.0,
            unit = "g",
            imageUrl = "https://images.unsplash.com/photo-1595855759920-86582396756a?auto=format&fit=crop&q=80&w=200",
            expiryDate = "2026-06-01",
            notes = "Hộc rau",
            price = 20000.0,
            kcal = 18
        )
    )

    override suspend fun addManualFood(
        name: String,
        quantity: Double,
        unit: String,
        category: String,
        compartment: String,
        imageUrl: String?,
        expiryDate: String,
        notes: String?,
        price: Double?,
        kcal: Int?
    ): Result<FridgeItem> {
        return try {
            val response = apiService.addManualFood(
                AddInventoryRequest(
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    category = category,
                    compartment = compartment,
                    imageUrl = imageUrl,
                    expiryDate = expiryDate,
                    notes = notes,
                    price = price,
                    kcal = kcal
                )
            )
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                val mockItem = FridgeItem(
                    id = (mockItems.size + 1).toString(),
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    imageUrl = imageUrl,
                    expiryDate = expiryDate,
                    notes = notes,
                    price = price,
                    kcal = kcal
                )
                Result.success(mockItem)
            }
        } catch (e: Exception) {
            val mockItem = FridgeItem(
                id = (mockItems.size + 1).toString(),
                name = name,
                quantity = quantity,
                unit = unit,
                imageUrl = imageUrl,
                expiryDate = expiryDate,
                notes = notes,
                price = price,
                kcal = kcal
            )
            Result.success(mockItem)
        }
    }

    override suspend fun getInventoryItems(): Result<List<FridgeItem>> {
        return try {
            val response = apiService.getInventoryItems()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.success(mockItems)
            }
        } catch (e: Exception) {
            Result.success(mockItems)
        }
    }

    override suspend fun getItemDetails(id: String): Result<FridgeItem> {
        return try {
            val response = apiService.getItemDetails(id)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.success(mockItems.first { it.id == id })
            } else {
                val mock = mockItems.firstOrNull { it.id == id }
                if (mock != null) Result.success(mock) else Result.failure(Exception("Not found"))
            }
        } catch (e: Exception) {
            val mock = mockItems.firstOrNull { it.id == id }
            if (mock != null) Result.success(mock) else Result.failure(e)
        }
    }

    override suspend fun deleteItem(id: String): Result<Unit> {
        return try {
            val response = apiService.deleteItem(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.success(Unit)
        }
    }
}

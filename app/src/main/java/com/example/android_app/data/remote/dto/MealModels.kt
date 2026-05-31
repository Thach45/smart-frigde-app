package com.example.android_app.data.remote.dto

data class SuggestFromItemRequest(
    val targetItemId: String
)

data class VoiceRequest(
    val text: String
)

data class VoiceResponse(
    val success: Boolean,
    val message: String,
    val ingredient: com.example.android_app.domain.model.FridgeItem? = null
)

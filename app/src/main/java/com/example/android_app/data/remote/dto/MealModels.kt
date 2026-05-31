package com.example.android_app.data.remote.dto

data class SuggestFromItemRequest(
    val targetItemId: String
)

data class VoiceRequest(
    val text: String
)

data class VoiceResponse(
    val reply: String
)

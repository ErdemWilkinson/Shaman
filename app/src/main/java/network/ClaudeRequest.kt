package com.erdem.shaman.network


data class ClaudeRequest(
    val model: String = "claude-haiku-4-5-20251001",
    @com.google.gson.annotations.SerializedName("max_tokens") val maxTokens: Int = 1024,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

data class ClaudeResponse(
    val content: List<ContentBlock>
)

data class ContentBlock(
    val type: String,
    val text: String?
)
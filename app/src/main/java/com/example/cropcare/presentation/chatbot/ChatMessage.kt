package com.example.cropcare.presentation.chatbot

data class ChatMessage(
    val message: String,
    val isUser: Boolean     // true = user, false = bot
)


package com.example.cropcare.presentation.history

data class HistoryModel(
    val id: Int,
    val imageUri: String,
    val disease: String,
    val confidence: String,
    val date: String,
    val fullResult: String
)

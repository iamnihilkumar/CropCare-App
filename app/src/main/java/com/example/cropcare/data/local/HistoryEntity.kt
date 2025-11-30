package com.example.cropcare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val disease: String,
    val confidence: String,
    val resultText: String,
    val dateTime: String
)


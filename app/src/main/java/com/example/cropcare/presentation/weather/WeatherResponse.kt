package com.example.cropcare.presentation.weather

data class WeatherResponse(
    val weather: List<WeatherInfo>,
    val main: WeatherMain
)

data class WeatherInfo(
    val description: String
)

data class WeatherMain(
    val temp: Double
)

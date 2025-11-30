package com.example.cropcare.presentation.weather

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("data/2.5/weather")
    fun getWeather(
        @Query("q") city: String,
        @Query("appid") key: String,
        @Query("units") units: String
    ): Call<WeatherResponse>
}

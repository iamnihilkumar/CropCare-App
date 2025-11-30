package com.example.cropcare.presentation.weather

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcare.R
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

class WeatherActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var tvCityName: TextView
    private lateinit var tvTemperature: TextView
    private lateinit var tvCondition: TextView
    private lateinit var weatherIcon: ImageView
    private lateinit var weatherCard: View
    private lateinit var btnBack: Button

    //  OpenWeatherMap API key
    private val API_KEY = "87aca02c57a803cdd2252c3da1b019d4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        initViews()

        btnSearch.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isEmpty()) {
                Toast.makeText(this, "Enter city name", Toast.LENGTH_SHORT).show()
            } else {
                fetchWeather(city)
            }
        }

        btnBack.setOnClickListener { finish() }
    }

    private fun initViews() {
        etCity = findViewById(R.id.etCity)
        btnSearch = findViewById(R.id.btnSearch)
        tvCityName = findViewById(R.id.tvCityName)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvCondition = findViewById(R.id.tvCondition)
        weatherIcon = findViewById(R.id.weatherIcon)
        weatherCard = findViewById(R.id.layoutWeatherCard)
        btnBack = findViewById(R.id.btnBack)

        weatherCard.alpha = 0f
    }

    private fun fetchWeather(city: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiUrl =
                    "https://api.openweathermap.org/data/2.5/weather?q=${city.trim()}&appid=$API_KEY&units=metric"

                val response = URL(apiUrl).readText()
                val json = JSONObject(response)

                val cityName = json.getString("name")
                val temp = json.getJSONObject("main").getDouble("temp")
                val condition = json.getJSONArray("weather")
                    .getJSONObject(0).getString("main")

                withContext(Dispatchers.Main) {
                    updateUI(cityName, temp, condition)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@WeatherActivity,
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun updateUI(city: String, temperature: Double, condition: String) {
        tvCityName.text = city
        tvTemperature.text = "${temperature.toInt()}°C"
        tvCondition.text = condition

        // Weather icons based on condition
        when (condition) {
            "Clear" -> weatherIcon.setImageResource(R.drawable.ic_sun)
            "Clouds" -> weatherIcon.setImageResource(R.drawable.ic_cloud)
            "Rain" -> weatherIcon.setImageResource(R.drawable.ic_rain)
            "Thunderstorm" -> weatherIcon.setImageResource(R.drawable.ic_storm)
            "Snow" -> weatherIcon.setImageResource(R.drawable.ic_snow)
            else -> weatherIcon.setImageResource(R.drawable.ic_weather)
        }

        // Fade animation
        weatherCard.animate()
            .alpha(1f)
            .setDuration(500)
            .start()
    }
}

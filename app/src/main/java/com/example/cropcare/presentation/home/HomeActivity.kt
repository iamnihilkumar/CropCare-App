package com.example.cropcare.presentation.home

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcare.R
import com.example.cropcare.presentation.history.HistoryActivity
import com.example.cropcare.presentation.profile.ProfileActivity
import com.example.cropcare.presentation.scan.ScanActivity
import com.example.cropcare.presentation.weather.WeatherActivity
import com.example.cropcare.presentation.chatbot.ChatbotActivity
import com.example.cropcare.presentation.news.NewsActivity
import com.example.cropcare.util.PrefManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var pref: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        pref = PrefManager(this)

        if (!pref.isLoggedIn()) {
            startActivity(Intent(this, com.example.cropcare.presentation.auth.LoginActivity::class.java))
            finish()
            return
        }

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val name = pref.getUserName() ?: "User"
        val formattedName = name.replaceFirstChar { it.uppercase() }
        tvWelcome.text = "Welcome, $formattedName 👋"

        findViewById<LinearLayout>(R.id.cardScan).setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardWeather).setOnClickListener {
            startActivity(Intent(this, WeatherActivity::class.java))
        }
        findViewById<LinearLayout>(R.id.cardNews).setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }

        val fabChat: FloatingActionButton = findViewById(R.id.btnChatBot)
        fabChat.startAnimation(android.view.animation.AnimationUtils.loadAnimation(this, R.anim.pulse_glow))
        fabChat.setOnClickListener {
            startActivity(Intent(this, ChatbotActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.selectedItemId = R.id.nav_home

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> true

                R.id.nav_scan -> {
                    startActivity(Intent(this, ScanActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

                else -> false
            }
        }
    }
}

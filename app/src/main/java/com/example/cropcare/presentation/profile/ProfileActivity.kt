package com.example.cropcare.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcare.R
import com.example.cropcare.presentation.auth.LoginActivity
import com.example.cropcare.presentation.home.HomeActivity
import com.example.cropcare.presentation.history.HistoryActivity
import com.example.cropcare.presentation.scan.ScanActivity
import com.example.cropcare.presentation.weather.WeatherActivity
import com.example.cropcare.util.PrefManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var pref: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        pref = PrefManager(this)

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val imgProfile = findViewById<ImageView>(R.id.imgProfile)

        val name = pref.getUserName() ?: "User"
        val email = pref.getUserEmail() ?: "Not Available"

        tvName.text = name
        tvEmail.text = email

        btnLogout.setOnClickListener {
            pref.logout()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        setupBottomNav()
    }

    private fun setupBottomNav() {
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavProfile)
        bottomNav.selectedItemId = R.id.nav_profile

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                    true
                }

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

                R.id.nav_profile -> true

                else -> false
            }
        }
    }
}

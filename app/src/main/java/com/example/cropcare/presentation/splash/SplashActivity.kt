package com.example.cropcare.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcare.databinding.ActivitySplashBinding
import com.example.cropcare.presentation.onboarding.OnboardingActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startFadeAnimation()
    }

    private fun startFadeAnimation() {
        binding.root.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(1000L)   // Smooth fade (1 second)
                .withEndAction {
                    goToOnboarding()
                }
                .start()
        }
    }

    private fun goToOnboarding() {
        // Important — keeping navigation lightweight & instant
        startActivity(Intent(this, OnboardingActivity::class.java))
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}


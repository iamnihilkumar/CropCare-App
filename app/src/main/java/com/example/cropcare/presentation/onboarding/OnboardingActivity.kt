package com.example.cropcare.presentation.onboarding

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import android.widget.Button
import com.example.cropcare.R
import com.example.cropcare.presentation.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var dotsLayout: LinearLayout
    private lateinit var btnNext: Button
    private lateinit var adapter: OnboardingAdapter

    private val items = listOf(
        OnboardingItem(R.drawable.onboard1, "Farm Yard", ""),
        OnboardingItem(R.drawable.onboard2, "sowing", ""),
        OnboardingItem(R.drawable.onboard3, "Harvesting", "")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.onboardingViewPager)
        dotsLayout = findViewById(R.id.layoutDots)
        btnNext = findViewById(R.id.btnNext)

        adapter = OnboardingAdapter(items)
        viewPager.adapter = adapter

        setupDots()
        updateDots(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                btnNext.text = if (position == items.lastIndex) "Get Started" else "Next"
            }
        })

        btnNext.setOnClickListener {
            val current = viewPager.currentItem
            if (current < items.lastIndex) {
                viewPager.currentItem = current + 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun setupDots() {
        val dots = Array(items.size) { ImageView(this) }
        val params = LinearLayout.LayoutParams(20, 20)
        params.setMargins(10, 0, 10, 0)

        dots.forEach {
            it.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))
            it.layoutParams = params
            dotsLayout.addView(it)
        }
    }

    private fun updateDots(position: Int) {
        for (i in 0 until dotsLayout.childCount) {
            val imageView = dotsLayout.getChildAt(i) as ImageView
            val drawable = if (i == position) R.drawable.dot_active else R.drawable.dot_inactive
            imageView.setImageDrawable(ContextCompat.getDrawable(this, drawable))
        }
    }
}

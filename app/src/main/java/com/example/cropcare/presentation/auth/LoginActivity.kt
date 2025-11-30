package com.example.cropcare.presentation.auth

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cropcare.R
import com.example.cropcare.presentation.home.HomeActivity
import com.example.cropcare.util.PrefManager

class LoginActivity : AppCompatActivity() {

    private lateinit var pref: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        pref = PrefManager(this)

        // If user already logged in → skip login
        if (pref.isLoggedIn()) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
            return
        }

        val emailEt = findViewById<EditText>(R.id.etEmail)
        val passEt = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvSignup = findViewById<TextView>(R.id.tvSignup)

        btnLogin.setOnClickListener {
            val email = emailEt.text.toString().trim()
            val password = passEt.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val savedEmail = pref.getUserEmail()
            val savedPassword = pref.getUserPassword()

            if (email == savedEmail && password == savedPassword) {
                // Use stored name if available, otherwise derive from email
                val name = pref.getUserName() ?: email.substringBefore("@")

                // Save user again to set isLoggedIn = true
                pref.saveUser(name, email, password)

                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid Credentials!", Toast.LENGTH_LONG).show()
            }
        }

        tvSignup.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
            finish()
        }
    }
}

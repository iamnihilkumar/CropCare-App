package com.example.cropcare.util

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("CropCarePrefs", Context.MODE_PRIVATE)

    fun saveUser(name: String, email: String, password: String) {
        prefs.edit()
            .putString("USER_NAME", name)
            .putString("USER_EMAIL", email)
            .putString("USER_PASSWORD", password)
            .putBoolean("IS_LOGGED_IN", true)
            .apply()
    }

    fun getUserName(): String? =
        prefs.getString("USER_NAME", null)

    fun getUserEmail(): String? =
        prefs.getString("USER_EMAIL", null)

    fun getUserPassword(): String? =
        prefs.getString("USER_PASSWORD", null)

    fun isLoggedIn(): Boolean =
        prefs.getBoolean("IS_LOGGED_IN", false)

    fun logout() {
        prefs.edit()
            .putBoolean("IS_LOGGED_IN", false)
            .apply()
    }

    fun clearUser() {
        prefs.edit().clear().apply()
    }
}

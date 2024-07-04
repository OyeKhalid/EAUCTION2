package com.example.eauction.persistence

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUserData(firstName: String, lastName: String, email: String, phone: String, userType: String) {
        val editor = preferences.edit()
        editor.putString("firstName", firstName)
        editor.putString("lastName", lastName)
        editor.putString("email", email)
        editor.putString("phone", phone)
        editor.putString("userType", userType)
        editor.apply()
    }

    fun getUserData(): Map<String, String?> {
        return mapOf(
            "firstName" to preferences.getString("firstName", null),
            "lastName" to preferences.getString("lastName", null),
            "email" to preferences.getString("email", null),
            "phone" to preferences.getString("phone", null),
            "userType" to preferences.getString("userType", null)
        )
    }

    fun clearUserData() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}

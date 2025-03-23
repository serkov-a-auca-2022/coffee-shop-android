package com.example.coffeeshopapp

import android.content.Context
import android.content.SharedPreferences
import com.example.coffeeshopapp.data.models.User
import com.google.gson.Gson

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUser(user: User) {
        prefs.edit()
            .putString("phone", user.phone)
            .putString("user_data", Gson().toJson(user))
            .apply()
    }

    fun getUser(): User? {
        val userData = prefs.getString("user_data", null) ?: return null
        return Gson().fromJson(userData, User::class.java)
    }

    fun getPhone(): String {
        return prefs.getString("phone", "") ?: ""
    }

    fun clearUser() {
        prefs.edit().clear().apply()
    }
}

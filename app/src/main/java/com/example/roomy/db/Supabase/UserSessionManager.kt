package com.example.roomy.db.Supabase

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.platform.LocalContext

object UserSessionManager {

    // Define SharedPreferences here
    private const val PREFS_NAME = "user_session"

    // Function to save session token
    fun saveSessionToken(context: Context, token: String) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.putString("auth_token", token)
        prefsEditor.apply()
    }

    // Function to get session token
    fun getSessionToken(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    // Function to clear session token
    fun clearSession(context: Context) {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val prefsEditor: SharedPreferences.Editor = sharedPreferences.edit()
        prefsEditor.remove("auth_token")
        prefsEditor.apply()
    }
}
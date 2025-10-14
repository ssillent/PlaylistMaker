package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

private const val THEME_PREFS_NAME = "theme_prefs"
private const val DARK_THEME_KEY = "dark_theme"

class App : Application() {

    var darkTheme = false
        private set

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(DARK_THEME_KEY, false)

        AppCompatDelegate.setDefaultNightMode(
            if (darkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled

        val sharedPreferences = getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, darkThemeEnabled)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
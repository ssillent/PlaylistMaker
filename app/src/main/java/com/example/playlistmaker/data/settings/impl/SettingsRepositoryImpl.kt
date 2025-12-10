package com.example.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.settings.repository.SettingsRepository
import com.example.playlistmaker.domain.settings.model.ThemeSettings

private const val DARK_THEME_KEY = "dark_theme"

class SettingsRepositoryImpl(private val sharedPreferences: SharedPreferences): SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val darkThemeEnabled = sharedPreferences.getBoolean(DARK_THEME_KEY, false)
        return ThemeSettings(darkThemeEnabled)
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, settings.darkthemeEnabled)
            .apply()

        applyTheme(settings.darkthemeEnabled)
    }

    override fun applyCurrentTheme() {
        val settings = getThemeSettings()
        applyTheme(settings.darkthemeEnabled)
    }

    private fun applyTheme(darkThemeEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled){
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
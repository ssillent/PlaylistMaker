package com.example.playlistmaker.data.repositoryImpl

import android.content.SharedPreferences
import com.example.playlistmaker.domain.repository.SettingsRepository

private const val DARK_THEME_KEY = "dark_theme"

class SettingsRepositoryImpl (private val sharedPreferences: SharedPreferences): SettingsRepository{
    override fun getDarkTheme(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }

    override fun setDarkTheme(enabled: Boolean) {
        sharedPreferences.edit()
            .putBoolean(DARK_THEME_KEY, enabled)
            .apply()
    }

}
package com.example.playlistmaker.data.settings.repository

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings

    fun updateThemeSettings(settings: ThemeSettings)

    fun applyCurrentTheme()
}
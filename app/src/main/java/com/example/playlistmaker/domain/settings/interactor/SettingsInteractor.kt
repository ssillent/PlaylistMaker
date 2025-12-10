package com.example.playlistmaker.domain.settings.interactor

import com.example.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsInteractor {

    fun getThemeSettings(): ThemeSettings
    fun updateThemeSettings(settings: ThemeSettings)
    fun applyCurrentTheme()

}
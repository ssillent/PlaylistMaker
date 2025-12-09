package com.example.playlistmaker.domain.settings.impl

import com.example.playlistmaker.data.settings.repository.SettingsRepository
import com.example.playlistmaker.domain.settings.interactor.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return repository.getThemeSettings()
    }

    override fun updateThemeSettings(settings: ThemeSettings) {
        repository.updateThemeSettings(settings)
    }

    override fun applyCurrentTheme() {
        repository.applyCurrentTheme()
    }
}
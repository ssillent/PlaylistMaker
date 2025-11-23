package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.repository.SettingsRepository

class SettingsInteractorImpl (private val repository: SettingsRepository): SettingsInteractor {

    override fun getDarkTheme(): Boolean {
        return repository.getDarkTheme()
    }

    override fun setDarkTheme(enabled: Boolean) {
        repository.setDarkTheme(enabled)
    }

    override fun applyCurrentTheme() {
        repository.applyCurrentTheme()
    }

}
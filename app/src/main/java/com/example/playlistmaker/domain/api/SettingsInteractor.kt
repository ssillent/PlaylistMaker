package com.example.playlistmaker.domain.api

interface SettingsInteractor {

    fun getDarkTheme(): Boolean
    fun setDarkTheme(enabled: Boolean)
    fun toggleTheme()

}
package com.example.playlistmaker.domain.repository

interface SettingsRepository {

    fun getDarkTheme() : Boolean
    fun setDarkTheme(enabled: Boolean)
    fun applyCurrentTheme()

}
package com.example.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.domain.settings.interactor.SettingsInteractor
import com.example.playlistmaker.domain.settings.model.ThemeSettings
import com.example.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.example.playlistmaker.ui.App

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {


      private val _themeSettings = MutableLiveData<ThemeSettings>()

      val themeSettings: LiveData<ThemeSettings> = _themeSettings

      init {
          loadThemeSettings()
      }

    private fun loadThemeSettings() {
        val settings = settingsInteractor.getThemeSettings()
        _themeSettings.value = settings
    }

    fun updateThemeSettings(darkThemeEnabled: Boolean) {
        val newSettings = ThemeSettings(darkThemeEnabled)
        settingsInteractor.updateThemeSettings(newSettings)
        _themeSettings.value = newSettings
    }

    fun shareLink() {
        sharingInteractor.shareLink()
    }

    fun openTerms(){
        sharingInteractor.openTerms()
    }
    fun openSupport(){
        sharingInteractor.openSupport()
    }
}
package com.example.playlistmaker.ui

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.settings.interactor.SettingsInteractor


class App : Application() {


    override fun onCreate() {
        super.onCreate()

        Creator.provideSettingsInteractor(this).applyCurrentTheme()
    }


}
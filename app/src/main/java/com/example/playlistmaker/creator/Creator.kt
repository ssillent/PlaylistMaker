package com.example.playlistmaker.creator

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.data.audioplayer.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.SharingDataProvider
import com.example.playlistmaker.domain.audioplayer.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.audioplayer.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.search.Impl.SearchInteractorImpl
import com.example.playlistmaker.domain.search.Interactor.SearchInteractor
import com.example.playlistmaker.domain.settings.interactor.SettingsInteractor
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.sharing.ExternalNavigator
import com.google.gson.Gson

object Creator {

    fun provideSearchInteractor(context: Context): SearchInteractor {
        val repository = getSearchRepository(context)

        return SearchInteractorImpl(repository)
    }

    private fun getSearchRepository(context: Context): SearchRepositoryImpl {
        val networkClient = RetrofitNetworkClient()
        val trackMapper = TrackMapper()
        val sharedPreferences = context.getSharedPreferences(
            "search_history",
            Context.MODE_PRIVATE
        )

        val gson = Gson()

        return SearchRepositoryImpl(
            networkClient = networkClient,
            trackMapper = trackMapper,
            sharedPreferences = sharedPreferences,
            gson = gson
        )
    }

    private fun getSettingsRepository(context: Context): SettingsRepositoryImpl {
        val sharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        return SettingsRepositoryImpl(sharedPreferences)
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        val repository = getSettingsRepository(context)
        return SettingsInteractorImpl(repository)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {

        val externalNavigator = ExternalNavigator(context)

        val sharingConfig = SharingDataProvider(context).getSharingConfig()

        return SharingInteractorImpl(externalNavigator, sharingConfig)
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        val repository = AudioPlayerRepositoryImpl()
        return AudioPlayerInteractorImpl(repository)
    }


}
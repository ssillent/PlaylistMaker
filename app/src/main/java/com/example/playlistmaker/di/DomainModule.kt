package com.example.playlistmaker.di

import com.example.playlistmaker.data.audioplayer.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.domain.audioplayer.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.audioplayer.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.audioplayer.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.search.Impl.SearchInteractorImpl
import com.example.playlistmaker.domain.search.Interactor.SearchInteractor
import com.example.playlistmaker.domain.search.Repository.SearchRepository
import com.example.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.playlistmaker.domain.settings.interactor.SettingsInteractor
import com.example.playlistmaker.domain.settings.repository.SettingsRepository
import com.example.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.playlistmaker.domain.sharing.interactor.SharingInteractor
import org.koin.dsl.module

val domainModule = module {

    single<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<SharingInteractor> {
        SharingInteractorImpl(get(), get())
    }

    single<AudioPlayerRepository> { get<AudioPlayerRepositoryImpl>() }
    single<SearchRepository> { get<SearchRepositoryImpl>() }
    single<SettingsRepository> { get<SettingsRepositoryImpl>() }
}
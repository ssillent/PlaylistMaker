package com.example.playlistmaker.di

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.data.audioplayer.impl.AudioPlayerRepositoryImpl
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.SongApiService
import com.example.playlistmaker.data.search.impl.SearchRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.sharing.SharingDataProvider
import com.example.playlistmaker.domain.sharing.model.StringLinks
import com.example.playlistmaker.ui.sharing.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<SongApiService> {
        get<Retrofit>().create(SongApiService::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<SharedPreferences>(named("search_prefs")) {
        androidContext().getSharedPreferences("search_history", Context.MODE_PRIVATE)
    }

    single<SharedPreferences>(named("theme_prefs")) {
        androidContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
    }

    factory { Gson() }
    factory { TrackMapper() }
    single { ExternalNavigator(androidContext()) }
    single { SharingDataProvider(androidContext()) }
    single<StringLinks> {
        get<SharingDataProvider>().getSharingConfig()
    }

    single<AudioPlayerRepositoryImpl> {
        AudioPlayerRepositoryImpl()
    }


    single<SearchRepositoryImpl> {
        SearchRepositoryImpl(
            networkClient = get(),
            trackMapper = get(),
            sharedPreferences = get(named("search_prefs")),
            gson = get()
        )
    }

    single<SettingsRepositoryImpl> {
        SettingsRepositoryImpl(get(named("theme_prefs")))
    }

}
package com.example.playlistmaker.di

import com.example.playlistmaker.ui.audioplayer.view_model.AudioPlayerViewModel
import com.example.playlistmaker.ui.edit_playlist.viewmodel.EditPlaylistViewModel
import com.example.playlistmaker.ui.make_playlist.view_model.MakePlaylistViewModel
import com.example.playlistmaker.ui.mediateka.view_model.FavoritesViewModel
import com.example.playlistmaker.ui.mediateka.view_model.PlaylistsViewModel
import com.example.playlistmaker.ui.playlist.view_model.MainPlaylistViewModel
import com.example.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { AudioPlayerViewModel(get(), get(), get()) }
    viewModel { SearchViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { PlaylistsViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { MakePlaylistViewModel(get()) }
    viewModel { MainPlaylistViewModel(get(), get(), get()) }
    viewModel { params -> EditPlaylistViewModel(get(), params.get())
    }

}
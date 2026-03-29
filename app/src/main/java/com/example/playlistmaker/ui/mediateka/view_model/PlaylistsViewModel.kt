package com.example.playlistmaker.ui.mediateka.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.make_playlist.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val makePlaylistInteractor: PlaylistInteractor) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    fun loadPlaylists() {
        viewModelScope.launch {
            makePlaylistInteractor.getAllPlaylists().collect { playlistList ->
                _playlists.value = playlistList
            }
        }
    }

}
package com.example.playlistmaker.ui.mediateka.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.launch

sealed class FavoritesState {
    object Empty : FavoritesState()

    data class Content(val tracks: List<Track>) : FavoritesState()
}

class FavoritesViewModel(private val favoritesInteractor: FavoriteTracksInteractor) : ViewModel(){

    private val _state = MutableLiveData<FavoritesState>()
    val state: LiveData<FavoritesState> = _state

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect{ tracks ->
                processResult(tracks)
            }
        }
    }

    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            _state.value = FavoritesState.Empty
        } else {
            _state.value = FavoritesState.Content(tracks)
        }
    }

}
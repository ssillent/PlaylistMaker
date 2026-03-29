package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.make_playlist.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.sharing.interactor.SharingInteractor
import kotlinx.coroutines.launch

data class PlaylistUiState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val totalDurationMinutes: Long = 0L,
    val tracksCount: Int = 0,
    val showEmptyShareMessage: Boolean = false,
    val navigateBack: Boolean = false
)

class MainPlaylistViewModel(
    private val interactor: PlaylistInteractor,
    private val playlistId: Int,
    private val sharingInteractor: SharingInteractor
    ): ViewModel() {

    private val _state = MutableLiveData<PlaylistUiState>()
    val state: LiveData<PlaylistUiState> = _state

    private var currentPlaylist: Playlist? = null
    private var currentTracks: List<Track> = emptyList()

    init {
        loadPlaylistData()
    }

    fun sharePlaylist() {
        viewModelScope.launch {
            val playlist = currentPlaylist
            val tracks = currentTracks

            if (playlist != null) {
                val success = sharingInteractor.sharePlaylist(playlist, tracks)
                if (!success) {
                    _state.value = _state.value?.copy(showEmptyShareMessage = true)
                }
            }
        }
    }

    fun resetEmptyShareMessage() {
        _state.value = _state.value?.copy(showEmptyShareMessage = false)
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            interactor.deletePlaylist(playlistId)
            _state.value = _state.value?.copy(navigateBack = true)
        }
    }

    private fun updateUiState(playlist: Playlist, tracks: List<Track>) {
        currentPlaylist = playlist
        currentTracks = tracks

        val totalMillis = interactor.calculateTotalDuration(tracks)
        val minutes = totalMillis / 60000

        _state.value = PlaylistUiState(
            playlist = playlist,
            tracks = tracks,
            totalDurationMinutes = minutes,
            tracksCount = tracks.size,
            showEmptyShareMessage = false,
            navigateBack = false
        )
    }

    fun refreshPlaylistData() {
        loadPlaylistData()
    }

    private fun loadPlaylistData() {
        viewModelScope.launch {
            val playlist = interactor.getPlaylistById(playlistId)

            if (playlist != null) {
                currentPlaylist = playlist

                val trackIds = playlist.tracksID.map { it.toLong() }

                if (trackIds.isNotEmpty()) {
                    interactor.getTracksByIds(trackIds).collect { tracks ->
                        val sortedTracks = trackIds.mapNotNull { id ->
                            tracks.find { it.trackId == id }
                        }.reversed()

                            updateUiState(playlist, sortedTracks)
                    }
                } else {
                    updateUiState(playlist, emptyList())
                }
            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            val playlist = currentPlaylist

            if (playlist != null) {
                val updatedTrackIDs = playlist.tracksID.filter { it != track.trackId.toInt() }
                val updatedPlaylist = playlist.copy(
                    tracksID = updatedTrackIDs,
                    tracksCount = updatedTrackIDs.count()
                )

                interactor.updatePlaylist(updatedPlaylist)
                currentPlaylist = updatedPlaylist
                interactor.cleanUnusedTrack(track.trackId)

                if (updatedTrackIDs.isNotEmpty()) {
                    interactor.getTracksByIds(updatedTrackIDs.map { it.toLong() }).collect { tracks ->
                        updateUiState(updatedPlaylist, tracks)
                    }
                } else {
                    updateUiState(updatedPlaylist, emptyList())
                }
            }
        }
    }
}
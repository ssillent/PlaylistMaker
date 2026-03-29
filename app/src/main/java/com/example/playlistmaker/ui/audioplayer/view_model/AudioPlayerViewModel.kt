package com.example.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.audioplayer.interactor.AudioPlayerInteractor
import com.example.playlistmaker.domain.db.FavoriteTracksInteractor
import com.example.playlistmaker.domain.make_playlist.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

enum class PlayerState {
    DEFAULT,
    PREPARED,
    PLAYING,
    PAUSED
}

data class AudioPlayerState(
    val playerState: PlayerState = PlayerState.DEFAULT,
    val progressTime: String = "00:00",
    val isFavorite: Boolean = false,
    val playlists: List<Playlist>? = null,
    val toastMessageResId: Int? = null,
    val toastArg: String? = null,
    val shouldCloseBottomSheet: Boolean = false
)

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor,
    private val favoritesInteractor: FavoriteTracksInteractor,
    private val makePlaylistInteractor: PlaylistInteractor
) : ViewModel() {

    companion object{
        private const val PROGRESS_DELAY = 300L
    }

    private val _state = MutableLiveData(AudioPlayerState())
    val state: LiveData<AudioPlayerState> = _state


    private var updateJob: Job? = null

    private lateinit var currentTrack: Track

    fun setTrack(track: Track) {
        currentTrack = track
        viewModelScope.launch {
            val isFavorite = favoritesInteractor.isFavorite(track.trackId)
            _state.value = _state.value?.copy(isFavorite = isFavorite)
        }
    }

    fun loadPlaylists(){
        viewModelScope.launch {
            makePlaylistInteractor.getAllPlaylists()
                .collect { playlists ->
                    _state.value = _state.value?.copy(playlists = playlists)
                }
        }
    }

    fun onPlaylistSelected(playlist: Playlist) {
        viewModelScope.launch {
            val isInPlaylist = makePlaylistInteractor.isTrackInPlaylist(
                playlist.playlistId,
                currentTrack.trackId
            )

            if (isInPlaylist) {
                _state.value = _state.value?.copy(
                    toastMessageResId = R.string.already_added,
                    toastArg = playlist.playlistName,
                    shouldCloseBottomSheet = false
                )
            } else {
                makePlaylistInteractor.addTrackToPlaylist(playlist, currentTrack)
                _state.value = _state.value?.copy(
                    toastMessageResId = R.string.successful_added,
                    toastArg = playlist.playlistName,
                    shouldCloseBottomSheet = true
                )
            }
        }
    }

    fun clearToast() {
        _state.value = _state.value?.copy(
            toastArg = null,
            toastMessageResId = null
        )
    }

    fun onBottomSheetClosed() {
        _state.value = _state.value.copy(shouldCloseBottomSheet = false)
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentIsFavorite = _state.value?.isFavorite ?: false

            if (currentIsFavorite) {
                favoritesInteractor.deleteFromFavorites(currentTrack)
            } else {
                favoritesInteractor.addToFavorites(currentTrack)
            }

            _state.value = _state.value?.copy(isFavorite = !currentIsFavorite)

            currentTrack = currentTrack.copy(isFavorite = !currentIsFavorite)
        }
    }

    fun preparePlayer(previewUrl: String) {
        interactor.setOnPreparedListener {
            _state.value = _state.value?.copy(
                playerState = PlayerState.PREPARED,
                progressTime = "00:00"
            )
        }

        interactor.setOnCompletionListener {
            viewModelScope.launch {
                stopProgressUpdates()
                _state.value = _state.value?.copy(
                    playerState = PlayerState.PREPARED,
                    progressTime = "00:00"
                )
            }
        }

        interactor.preparePlayer(previewUrl)
    }

    fun onPlayButtonClicked() {

        val currentState = _state.value?.playerState ?: return

        when (currentState) {
            PlayerState.PLAYING -> {
                interactor.pause()
                _state.value = _state.value?.copy(playerState = PlayerState.PAUSED)
                stopProgressUpdates()
            }
            PlayerState.PREPARED, PlayerState.PAUSED -> {
                interactor.play()
                _state.value = _state.value?.copy(playerState = PlayerState.PLAYING)
                startProgressUpdates()
            }
            PlayerState.DEFAULT -> {}
        }
    }

    fun onPause() {
        if (interactor.isPlaying()) {
            interactor.pause()
            _state.value = _state.value?.copy(playerState = PlayerState.PAUSED)
            stopProgressUpdates()
        }
    }

    private fun startProgressUpdates() {

        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                val position = interactor.getCurrentPosition()
                val progressTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)
                _state.value = _state.value?.copy(progressTime = progressTime)

                if (!interactor.isPlaying() && _state.value?.playerState == PlayerState.PLAYING) {
                    _state.value = _state.value?.copy(playerState = PlayerState.PAUSED)
                    break
                }

                delay(PROGRESS_DELAY)
            }
        }

    }

    private fun stopProgressUpdates() {
        updateJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
        interactor.releasePlayer()
    }

}



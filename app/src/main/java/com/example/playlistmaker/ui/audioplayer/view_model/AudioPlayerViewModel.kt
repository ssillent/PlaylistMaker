package com.example.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.audioplayer.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.audioplayer.interactor.AudioPlayerInteractor
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
    val progressTime: String = "00:00"
)

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor
) : ViewModel() {


    private val _state = MutableLiveData(AudioPlayerState())
    val state: LiveData<AudioPlayerState> = _state


    private var updateJob: Job? = null

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

                delay(300L)
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

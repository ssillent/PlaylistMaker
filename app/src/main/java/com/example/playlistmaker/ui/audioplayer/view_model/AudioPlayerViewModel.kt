package com.example.playlistmaker.ui.audioplayer.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.audioplayer.impl.AudioPlayerInteractorImpl
import com.example.playlistmaker.domain.audioplayer.interactor.AudioPlayerInteractor
import com.example.playlistmaker.ui.App
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable.isActive
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerViewModel(
    private val interactor: AudioPlayerInteractor
) : ViewModel() {

    companion object {
        const val STATE_DEFAULT = 0
        const val STATE_PREPARED = 1
        const val STATE_PLAYING = 2
        const val STATE_PAUSED = 3

        fun getFactory(): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as App
                    val interactor = Creator.provideAudioPlayerInteractor()
                    AudioPlayerViewModel(interactor)
                }
            }
    }

    private val _playerState = MutableLiveData(STATE_DEFAULT)
    val playerState: LiveData<Int> = _playerState

    private val _progressTime = MutableLiveData("00:00")
    val progressTime: LiveData<String> = _progressTime

    private var updateJob: Job? = null

    fun preparePlayer(previewUrl: String) {
        (interactor as? AudioPlayerInteractorImpl)
            ?.setOnPreparedListener {
                _playerState.value = STATE_PREPARED
            }

        interactor.preparePlayer(previewUrl)
    }

    fun onPlayButtonClicked() {
        when (_playerState.value) {
            STATE_PLAYING -> {
                interactor.pause()
                _playerState.value = STATE_PAUSED
                stopProgressUpdates()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                interactor.play()
                _playerState.value = STATE_PLAYING
                startProgressUpdates()
            }
            else -> {}
        }
    }

    fun onPause() {
        if (interactor.isPlaying()) {
            interactor.pause()
            _playerState.value = STATE_PAUSED
            stopProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (isActive) {
                val position = interactor.getCurrentPosition()
                _progressTime.value = SimpleDateFormat("mm:ss", Locale.getDefault()).format(position)

                if (!interactor.isPlaying() && _playerState.value == STATE_PLAYING) {
                    _playerState.value = STATE_PAUSED
                    break
                }

                delay(200L)
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

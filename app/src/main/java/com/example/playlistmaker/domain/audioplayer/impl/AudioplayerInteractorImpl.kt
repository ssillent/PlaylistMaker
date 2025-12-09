package com.example.playlistmaker.domain.audioplayer.impl

import com.example.playlistmaker.data.audioplayer.repository.AudioPlayerRepository
import com.example.playlistmaker.domain.audioplayer.interactor.AudioPlayerInteractor

class AudioPlayerInteractorImpl(
    private val repository: AudioPlayerRepository
) : AudioPlayerInteractor {

    private var isPrepared = false
    private var onPreparedCallback: (() -> Unit)? = null

    override fun preparePlayer(previewUrl: String) {
        repository.preparePlayer(previewUrl) {
            isPrepared = true
            onPreparedCallback?.invoke()
        }
    }

    fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedCallback = listener
    }

    override fun play() {
        if (isPrepared) {
            repository.play()
        }
    }

    override fun pause() {
        repository.pause()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun getDuration(): Int {
        return repository.getDuration()
    }

    override fun releasePlayer() {
        repository.releasePlayer()
        isPrepared = false
    }

    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }
}
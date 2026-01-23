package com.example.playlistmaker.domain.audioplayer.interactor

interface AudioPlayerInteractor {

    fun preparePlayer(previewUrl: String)

    fun setOnCompletionListener(listener: () -> Unit)

    fun setOnPreparedListener(listener: () -> Unit)

    fun play()

    fun pause()

    fun getCurrentPosition(): Int

    fun getDuration(): Int

    fun releasePlayer()

    fun isPlaying(): Boolean
}
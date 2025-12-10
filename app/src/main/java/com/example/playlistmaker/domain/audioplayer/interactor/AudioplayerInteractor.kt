package com.example.playlistmaker.domain.audioplayer.interactor

interface AudioPlayerInteractor {

    fun preparePlayer(previewUrl: String)

    fun play()

    fun pause()

    fun getCurrentPosition(): Int

    fun getDuration(): Int

    fun releasePlayer()

    fun isPlaying(): Boolean
}
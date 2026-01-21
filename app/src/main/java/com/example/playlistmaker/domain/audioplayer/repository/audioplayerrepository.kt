package com.example.playlistmaker.domain.audioplayer.repository

interface AudioPlayerRepository {

    fun preparePlayer(previewUrl: String, onPrepared: () -> Unit)

    fun setOnCompletionListener(listener: () -> Unit)

    fun play()

    fun pause()

    fun getCurrentPosition(): Int

    fun getDuration(): Int

    fun releasePlayer()

    fun isPlaying(): Boolean
}
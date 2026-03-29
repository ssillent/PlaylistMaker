package com.example.playlistmaker.domain.sharing.interactor

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.sharing.model.EmailData

interface SharingInteractor {

    fun shareLink()
    fun openTerms()
    fun openSupport()
    fun shareText(text: String)

    suspend fun sharePlaylist(playlist: Playlist, tracks: List<Track>): Boolean

    fun getShareLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): EmailData
}


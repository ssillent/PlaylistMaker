package com.example.playlistmaker.domain.sharing.impl

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.sharing.ExternalNavigator
import com.example.playlistmaker.domain.sharing.interactor.SharingInteractor
import com.example.playlistmaker.domain.sharing.model.EmailData
import com.example.playlistmaker.domain.sharing.model.StringLinks
import com.example.playlistmaker.utils.formatTracks

class SharingInteractorImpl(private val externalNavigator: ExternalNavigator, private val stringLinks: StringLinks): SharingInteractor {

    override fun shareLink() {
        externalNavigator.shareLink(getShareLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    override fun shareText(text: String) {
        externalNavigator.shareText(text)
    }

    override suspend fun sharePlaylist(playlist: Playlist, tracks: List<Track>): Boolean {
        if (tracks.isEmpty()) {
            return false
        }

        shareText(buildShareText(playlist, tracks))

        return true
    }

    private fun buildShareText(playlist: Playlist, tracks: List<Track>): String {
        return buildString {
            append(playlist.playlistName).append("\n")

            if (!playlist.playlistDescription.isNullOrEmpty()) {
                append(playlist.playlistDescription).append("\n")
            }

            append(tracks.size.formatTracks()).append("\n")

            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis}) \n")
            }
        }.trimEnd()
    }

    override fun getShareLink(): String {
        return stringLinks.shareLink
    }

    override fun getTermsLink(): String {
        return stringLinks.termsLink
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = stringLinks.supportEmail,
            subject = stringLinks.supportSubject,
            body = stringLinks.supportBody
        )
    }
}
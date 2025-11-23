package com.example.playlistmaker.domain.models

import java.io.Serializable

data class Track(
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val artworkUrl512: String,
    val trackId: Long,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String

) : Serializable{
    fun getUpdatedArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getReleaseYear() : String? {
        return releaseDate?.substring(0, 4)
    }
}


package com.example.playlistmaker

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTimeMillis: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String,
    @SerializedName("trackId") val trackId: Long,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("primaryGenreName") val primaryGenreName: String,
    @SerializedName("country") val country: String

) : Serializable{
    fun getUpdatedArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")

    fun getReleaseYear() : String? {
        return releaseDate?.substring(0, 4)
    }
}


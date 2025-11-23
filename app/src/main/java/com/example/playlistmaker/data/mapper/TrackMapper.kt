package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackMapper {

    private fun DtoToDomain(dto: TrackDto): Track {
        return Track(
            trackName = dto.trackName,
            artistName = dto.artistName,
            trackTimeMillis = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(dto.trackTimeMillis)),
            artworkUrl100 = dto.artworkUrl100,
            artworkUrl512 = getUpdatedArtwork(dto.artworkUrl100),
            trackId = dto.trackId,
            collectionName = dto.collectionName,
            releaseDate = getReleaseYear(dto.releaseDate),
            primaryGenreName = dto.primaryGenreName,
            country = dto.country,
            previewUrl = dto.previewUrl
        )
    }

    fun listDtoToDomain(dtos: List<TrackDto>): List<Track> {
        return dtos.map { DtoToDomain(it) }
    }

    fun getUpdatedArtwork(artworkUrl100: String): String{
        return artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    }

    fun getReleaseYear(releaseDate: String?) : String? {
        return releaseDate?.substring(0, 4)
    }

}
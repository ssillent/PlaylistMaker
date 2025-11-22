package com.example.playlistmaker.data.dto
import com.example.playlistmaker.domain.models.Track
import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("resultCount") val resultCount: Int,
    @SerializedName("results") val results: List<TrackDto>
) : Response()

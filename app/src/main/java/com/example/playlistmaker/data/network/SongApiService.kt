package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.TrackResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApiService {
    @GET("/search?entity=song")
    suspend fun searchTrack(@Query("term") text: String): TrackResponse
}


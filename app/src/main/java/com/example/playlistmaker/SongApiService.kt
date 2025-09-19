package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SongApiService {
    @GET("/search?entity=song")
    fun searchTrack(@Query("term") text: String): Call<TrackResponse>
}


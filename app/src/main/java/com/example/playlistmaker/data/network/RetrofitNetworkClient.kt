package com.example.playlistmaker.data.network

import android.content.Context
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val songApiService: SongApiService) : NetworkClient{


    override fun doRequest(dto: Any): Response {
        if (dto is TrackRequest) {
            val resp = songApiService.searchTrack(dto.expression).execute()

            val body = resp.body() ?: Response()

            return body.apply { resultCode = resp.code() }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }

}
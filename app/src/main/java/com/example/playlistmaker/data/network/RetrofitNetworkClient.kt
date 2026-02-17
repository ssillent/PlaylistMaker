package com.example.playlistmaker.data.network

import android.content.Context
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackRequest
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient(private val songApiService: SongApiService) : NetworkClient{


    override suspend fun doRequest(dto: Any): Response {
        return try {

            if (dto is TrackRequest) {
                val response = songApiService.searchTrack(dto.expression)

                response.apply { resultCode = 200 }

            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: IOException) {
            Response().apply { resultCode = 500 }
        } catch (e: HttpException) {
            Response().apply { resultCode = e.code() }
        } catch (e: Exception) {
            Response().apply { resultCode = 500 }
        }
    }

}
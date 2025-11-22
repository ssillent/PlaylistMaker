package com.example.playlistmaker.data.repositoryImpl

import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.repository.TrackRepository
import com.example.playlistmaker.domain.models.Track

class TrackRepositoryImpl (private val networkClient: NetworkClient, private val mapper: TrackMapper):
    TrackRepository {

    override fun searchTrack(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackRequest(expression))
        if (response.resultCode == 200) {
            return mapper.listDtoToDomain((response as TrackResponse).results)
        } else {
            return emptyList()
        }
    }

}
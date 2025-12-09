package com.example.playlistmaker.data.search.Repository

import com.example.playlistmaker.domain.models.Track

interface SearchRepository {

    suspend fun searchTracks(query: String): List<Track>

    suspend fun getSearchHistory(): List<Track>

    suspend fun addToHistory(track: Track)

    suspend fun clearHistory()

    suspend fun saveSearchHistory(tracks: List<Track>)
}
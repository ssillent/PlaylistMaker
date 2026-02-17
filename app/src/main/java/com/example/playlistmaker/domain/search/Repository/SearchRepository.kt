package com.example.playlistmaker.domain.search.Repository

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {

    fun searchTracks(query: String): Flow<List<Track>>

    suspend fun getSearchHistory(): List<Track>

    suspend fun addToHistory(track: Track)

    suspend fun clearHistory()

    suspend fun saveSearchHistory(tracks: List<Track>)
}
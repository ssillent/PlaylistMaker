package com.example.playlistmaker.domain.search.Interactor

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {

    fun searchTracks(query: String): Flow<SearchResult>

    suspend fun getSearchHistory(): List<Track>

    suspend fun addToHistory(track: Track)

    suspend fun clearHistory()

}
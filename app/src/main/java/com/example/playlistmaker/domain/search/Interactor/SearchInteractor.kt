package com.example.playlistmaker.domain.search.Interactor

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.model.SearchResult

interface SearchInteractor {

    suspend fun searchTracks(query: String): SearchResult

    suspend fun getSearchHistory(): List<Track>

    suspend fun addToHistory(track: Track)

    suspend fun clearHistory()

}
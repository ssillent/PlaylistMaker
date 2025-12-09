package com.example.playlistmaker.domain.search.Impl

import com.example.playlistmaker.data.search.Repository.SearchRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.Interactor.SearchInteractor
import com.example.playlistmaker.domain.search.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchInteractorImpl(private val repository: SearchRepository): SearchInteractor {

    override suspend fun searchTracks(query: String): SearchResult =
        withContext(Dispatchers.IO) {
        try {
            if (query.isBlank()) {
                SearchResult.Empty
            } else {
                val tracks = repository.searchTracks(query)
                if (tracks.isEmpty()) {
                    SearchResult.Empty
                } else {
                    SearchResult.Success(tracks)
                }
            }
        } catch (e: Exception) {
            SearchResult.Error
        }
    }

    override suspend fun getSearchHistory(): List<Track> =
        withContext(Dispatchers.IO) {
            repository.getSearchHistory()
        }

    override suspend fun addToHistory(track: Track) =
        withContext(Dispatchers.IO) {
            repository.addToHistory(track)
        }

    override suspend fun clearHistory() =
        withContext(Dispatchers.IO) {
            repository.clearHistory()
        }

}
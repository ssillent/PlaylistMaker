package com.example.playlistmaker.domain.search.Impl

import com.example.playlistmaker.domain.search.Repository.SearchRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.Interactor.SearchInteractor
import com.example.playlistmaker.domain.search.model.SearchResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SearchInteractorImpl(private val repository: SearchRepository): SearchInteractor {

    override fun searchTracks(query: String): Flow<SearchResult> =
        repository.searchTracks(query)
            .map { tracks ->
                if (tracks.isEmpty()) {
                    SearchResult.Empty
                }else{
                    SearchResult.Success(tracks)
                }
            }
            .catch { e ->
                e.printStackTrace()
                emit(SearchResult.Error)
            }
            .flowOn(Dispatchers.IO)

    override suspend fun getSearchHistory(): List<Track> = repository.getSearchHistory()

    override suspend fun addToHistory(track: Track) = repository.addToHistory(track)

    override suspend fun clearHistory() = repository.clearHistory()

}
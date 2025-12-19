package com.example.playlistmaker.ui.search.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.search.Interactor.SearchInteractor
import com.example.playlistmaker.domain.search.model.SearchResult

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchViewModel(private val searchInteractor: SearchInteractor): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L

    }

    private val _searchState = MutableLiveData<SearchState>(SearchState.Default)
    val searchState: LiveData<SearchState> = _searchState


    private var searchJob: Job? = null
    private var debounceJob: Job? = null
    private var lastSearchQuery: String = ""

    fun searchDebounce(query: String) {
        if (query == lastSearchQuery) return

        lastSearchQuery = query
        debounceJob?.cancel()

        debounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(query)
        }
    }

    fun searchWithoutDebounce(query: String) {
        debounceJob?.cancel()
        search(query)
    }

    fun loadSearchHistory() {
        viewModelScope.launch {
            _searchState.value = SearchState.LoadingHistory

            searchJob?.cancel()
            debounceJob?.cancel()

            try {
                val history = searchInteractor.getSearchHistory()

                _searchState.value = if (history.isEmpty()) {
                    SearchState.Default
                } else {
                    SearchState.History(history)
                }
            } catch (e: Exception) {
                _searchState.value = SearchState.Error
            }
        }
    }

    fun addToHistory(track: Track) {
        viewModelScope.launch {
            searchInteractor.addToHistory(track)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchInteractor.clearHistory()
            _searchState.value = SearchState.Default
        }
    }


    private fun search(query: String) {
        searchJob?.cancel()

        if (query.isBlank()) {
            loadSearchHistory()
            return
        }

        searchJob = viewModelScope.launch {
            _searchState.value = SearchState.Loading

            val buisnessResult = searchInteractor.searchTracks(query)

            val uiState = when (buisnessResult) {
                is SearchResult.Success -> SearchState.Content(buisnessResult.tracks)
                SearchResult.Empty -> SearchState.Empty
                SearchResult.Error -> SearchState.Error
            }

            _searchState.value = uiState
        }
    }

}

sealed class SearchState {

    object Default : SearchState()

    object LoadingHistory : SearchState()

    object Loading : SearchState()

    data class History(val tracks: List<Track>) : SearchState()

    data class Content(val tracks: List<Track>) : SearchState()

    object Empty : SearchState()

    object Error : SearchState()
}
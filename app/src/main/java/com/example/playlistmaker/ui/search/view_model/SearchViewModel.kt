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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch


class SearchViewModel(private val searchInteractor: SearchInteractor): ViewModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
        private const val CLICK_DEBOUNCE_DELAY = 500L

    }

    private val _searchState = MutableLiveData<SearchState>(SearchState.Default)
    val searchState: LiveData<SearchState> = _searchState


    private var searchJob: Job? = null
    private var debounceJob: Job? = null
    private var clickDebounceJob: Job? = null
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

    fun onTrackClickDebounce(track: Track, onNavigate: (Track) -> Unit) {

        if (clickDebounceJob?.isActive == true) {
            return
        }

        clickDebounceJob = viewModelScope.launch {
            searchInteractor.addToHistory(track)
            onNavigate(track)
            delay(CLICK_DEBOUNCE_DELAY)
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


    fun clearHistory() {
        viewModelScope.launch {
            searchInteractor.clearHistory()
            _searchState.value = SearchState.Default
        }
    }


    private fun search(query: String) {
        if (query.isBlank()) {
            loadSearchHistory()
            return
        }

        searchJob?.cancel()

        searchJob = searchInteractor.searchTracks(query)
            .onStart {
                _searchState.value = SearchState.Loading
            }
            .onEach { result ->
                val state = when (result) {
                    is SearchResult.Success -> SearchState.Content(result.tracks)
                    SearchResult.Empty -> SearchState.Empty
                    SearchResult.Error -> SearchState.Error
                }
                _searchState.value = state
            }
            .catch { e ->
                e.printStackTrace()
                _searchState.value = SearchState.Error
            }
            .launchIn(viewModelScope)
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

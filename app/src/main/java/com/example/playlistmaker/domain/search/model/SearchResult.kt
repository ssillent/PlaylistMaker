package com.example.playlistmaker.domain.search.model

import com.example.playlistmaker.domain.models.Track

sealed class SearchResult {

    data class Success(val tracks: List<Track>) : SearchResult()

    object Empty : SearchResult()

    object Error : SearchResult()

}
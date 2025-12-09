package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.search.Repository.SearchRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val HISTORY_KEY = "search_history"
private const val MAX_HISTORY_SIZE = 10

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper,
    private val sharedPreferences: SharedPreferences
): SearchRepository{

    private val gson = Gson()

    override suspend fun searchTracks(query: String): List<Track> =
        withContext(Dispatchers.IO) {
            val response = networkClient.doRequest(TrackRequest(query))

            if (response.resultCode == 200) {
                trackMapper.listDtoToDomain((response as TrackResponse).results)
            } else {
                emptyList()
            }
        }

    override suspend fun getSearchHistory(): List<Track> =
        withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(HISTORY_KEY, null)

            if (json != null) {
                val type = object : TypeToken<List<Track>>() {}.type
                gson.fromJson<List<Track>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }
    }

    override suspend fun addToHistory(track: Track) = withContext(Dispatchers.IO) {
        val currentHistory = getSearchHistory().toMutableList()

        currentHistory.removeAll { it.trackId == track.trackId }

        currentHistory.add(0, track)

        if (currentHistory.size > MAX_HISTORY_SIZE) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        saveSearchHistory(currentHistory)
    }

    override suspend fun clearHistory() = withContext(Dispatchers.IO) {
        sharedPreferences.edit {
            remove(HISTORY_KEY)
        }
    }

    override suspend fun saveSearchHistory(tracks: List<Track>) = withContext(Dispatchers.IO) {
        val json = gson.toJson(tracks)
        sharedPreferences.edit {
            putString(HISTORY_KEY, json)
            apply()
        }
    }



}
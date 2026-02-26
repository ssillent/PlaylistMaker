package com.example.playlistmaker.data.search.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.convertor.TrackMapper
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.search.Repository.SearchRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

private const val HISTORY_KEY = "search_history"
private const val MAX_HISTORY_SIZE = 10

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
): SearchRepository {


    override fun searchTracks(query: String): Flow<List<Track>> = flow {

        val response = networkClient.doRequest(TrackRequest(query))

        val tracks = if (response.resultCode == 200) {
            val trackList = trackMapper.listDtoToDomain((response as TrackResponse).results)
            markFavoriteTracks(trackList)
        } else {
            emptyList()
        }

        emit(tracks)
    }.flowOn(Dispatchers.IO)

    override suspend fun getSearchHistory(): List<Track> =
        withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(HISTORY_KEY, null)

           val historyTracks = if (json != null) {
                val type = object : TypeToken<List<Track>>() {}.type
                gson.fromJson<List<Track>>(json, type) ?: emptyList()
            } else {
                emptyList()
            }

            markFavoriteTracks(historyTracks)
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

    private suspend fun markFavoriteTracks(tracks: List<Track>): List<Track> {
        if (tracks.isEmpty()) return tracks

        val favoriteId = appDatabase.trackDao().getAllFavoriteTracksId()

        return tracks.map { track ->
            track.copy(isFavorite = track.trackId in favoriteId)
        }
    }



}
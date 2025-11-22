package com.example.playlistmaker.data.repositoryImpl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

private const val historyKey = "search_history"
private const val maxHistorySize = 10

class SearchHistoryRepositoryImpl (private val sharedPreferences: SharedPreferences):
    SearchHistoryRepository {
    private val gson = Gson()

    override fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
        else {
            emptyList()
        }
    }

    override fun addToHistory(track: Track){
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)

        if (currentHistory.size > maxHistorySize) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        saveHistory(currentHistory)

    }

    override fun clearHistory(){
        sharedPreferences.edit {
            remove(historyKey)
        }
    }

    private fun saveHistory(history: List<Track>){
        val json = gson.toJson(history)
        sharedPreferences.edit{
            putString(historyKey, json)
        }
    }
}
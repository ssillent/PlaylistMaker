package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    private val gson = Gson()
    private val historyKey = "search_history"
    private val maxHistorySize = 10

    fun getHistory(): List<Track> {
        val json = sharedPreferences.getString(historyKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<Track>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        }
        else {
            emptyList()
        }
    }

    fun addToHistory(track: Track){
        val currentHistory = getHistory().toMutableList()
        currentHistory.removeAll { it.trackId == track.trackId }
        currentHistory.add(0, track)

        if (currentHistory.size > maxHistorySize) {
            currentHistory.removeAt(currentHistory.size - 1)
        }

        saveHistory(currentHistory)

    }

    fun clearHistory(){
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


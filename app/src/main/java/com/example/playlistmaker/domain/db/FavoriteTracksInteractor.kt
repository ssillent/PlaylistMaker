package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    suspend fun addToFavorites(track: Track)

    suspend fun deleteFromFavorites(track: Track)

    fun getFavorites(): Flow<List<Track>>

    suspend fun isFavorite(trackId: Long): Boolean

}
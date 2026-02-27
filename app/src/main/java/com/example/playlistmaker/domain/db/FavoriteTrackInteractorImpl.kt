package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl (private val repository: FavoriteTracksRepository) : FavoriteTracksInteractor {

    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track)
    }

    override suspend fun deleteFromFavorites(track: Track) {
        repository.deleteFromFavorites(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return repository.getFavorites()
    }

    override suspend fun isFavorite(trackId: Long): Boolean {

        return repository.isFavorite(trackId)
    }
}
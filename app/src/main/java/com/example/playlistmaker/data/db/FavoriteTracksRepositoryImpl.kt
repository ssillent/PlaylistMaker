package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.convertor.TrackDbConvertor
import com.example.playlistmaker.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(private val appDatabase: AppDatabase, private val trackDbConvertor: TrackDbConvertor): FavoriteTracksRepository {

    override suspend fun addToFavorites(track: Track) {

        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().addFavoriteTrack(trackEntity)

    }

    override suspend fun deleteFromFavorites(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteFavoriteTrack(trackEntity)
    }

    override suspend fun isFavorite(trackId: Long): Boolean {
        return appDatabase.trackDao().isTrackFavorite(trackId)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return appDatabase.trackDao().getAllFavoriteTracks()
            .map { entities -> convertFromTrackEntity(entities) }
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }



}
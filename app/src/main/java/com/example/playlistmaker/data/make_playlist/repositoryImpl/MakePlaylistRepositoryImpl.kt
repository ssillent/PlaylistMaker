package com.example.playlistmaker.data.make_playlist.repositoryImpl

import com.example.playlistmaker.data.convertor.PlaylistDbConvertor
import com.example.playlistmaker.data.convertor.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.make_playlist.repository.MakePlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MakePlaylistRepositoryImpl(private val appDatabase: AppDatabase, private val playlistDbConvertor: PlaylistDbConvertor, private val trackDbConvertor: TrackDbConvertor) :
    MakePlaylistRepository {

    override suspend fun createPlaylist(playlist: Playlist) {
        val entity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().insertPlaylist(entity)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        val entity = playlistDbConvertor.map(playlist)
        appDatabase.playlistDao().updatePlaylist(entity)
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return appDatabase.playlistDao().getPlaylistById(id)?.let { entity ->
            playlistDbConvertor.map(entity)
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return appDatabase.playlistDao().getAllPlaylists().map { entities ->
            entities.map { entity ->
                playlistDbConvertor.map(entity)
            }
        }

    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val trackEntity = trackDbConvertor.mapToTrackForPlaylistEntity(track)
        appDatabase.playlistTrackDao().insertTrack(trackEntity)

        val updatedTracksId = playlist.tracksID + track.trackId.toInt()

        val updatedPlaylist = playlist.copy(
            tracksID = updatedTracksId,
            tracksCount = playlist.tracksCount + 1
        )

        updatePlaylist(updatedPlaylist)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Long): Boolean {
        val playlist = getPlaylistById(playlistId) ?: return false
        return playlist.tracksID.contains(trackId.toInt())
    }
}
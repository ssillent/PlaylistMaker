package com.example.playlistmaker.domain.make_playlist.impl

import com.example.playlistmaker.domain.make_playlist.interactor.PlaylistInteractor
import com.example.playlistmaker.domain.make_playlist.repository.PlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val repository: PlaylistRepository
) : PlaylistInteractor{

    override suspend fun createPlaylist(playlist: Playlist) {
        repository.createPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override suspend fun getPlaylistById(id: Int): Playlist? {
        return repository.getPlaylistById(id)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        repository.addTrackToPlaylist(playlist, track)
    }

    override suspend fun isTrackInPlaylist(playlistId: Int, trackId: Long): Boolean {
        return repository.isTrackInPlaylist(playlistId, trackId)
    }

    override fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>> {
        return repository.getTracksByIds(trackIds)
    }

    override fun calculateTotalDuration(tracks: List<Track>): Long {
        return tracks.sumOf { parseTimeToMillis(it.trackTimeMillis) }
    }

    override suspend fun cleanUnusedTrack(trackId: Long) {
        repository.cleanUnusedTrack(trackId)
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        repository.deletePlaylist(playlistId)
    }

    private fun parseTimeToMillis(timeString: String): Long {
        return try {
            val parts = timeString.split(":")
            if (parts.size == 2) {
                val minutes = parts[0].toLong()
                val seconds = parts[1].toLong()
                (minutes * 60 + seconds) * 1000
            } else {
                timeString.toLong()
            }
        } catch (e: Exception) {
            0L
        }
    }
}
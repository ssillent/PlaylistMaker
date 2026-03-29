package com.example.playlistmaker.domain.make_playlist.repository

import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun createPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun getPlaylistById(id: Int): Playlist?

    fun getAllPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track)

    suspend fun isTrackInPlaylist(playlistId: Int, trackId: Long): Boolean

    fun getTracksByIds(trackIds: List<Long>): Flow<List<Track>>

    suspend fun cleanUnusedTrack(trackId: Long)

    suspend fun deletePlaylist(playlistId: Int)
}
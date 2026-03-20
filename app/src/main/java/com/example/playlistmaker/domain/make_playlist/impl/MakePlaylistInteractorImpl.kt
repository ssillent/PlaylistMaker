package com.example.playlistmaker.domain.make_playlist.impl

import com.example.playlistmaker.domain.make_playlist.interactor.MakePlaylistInteractor
import com.example.playlistmaker.domain.make_playlist.repository.MakePlaylistRepository
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class MakePlaylistInteractorImpl(
    private val repository: MakePlaylistRepository
) : MakePlaylistInteractor{

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

}
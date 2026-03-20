package com.example.playlistmaker.data.convertor

import com.example.playlistmaker.data.db.PlaylistEntity
import com.example.playlistmaker.domain.models.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConvertor (private val gson: Gson){

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlistId = playlist.playlistId,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            path = playlist.path,
            tracksID = gson.toJson(playlist.tracksID),
            tracksCount = playlist.tracksCount
        )
    }

    fun map(entity: PlaylistEntity): Playlist {
        val type = object : TypeToken<List<Int>>() {}.type
        val trackIds: List<Int> = gson.fromJson(entity.tracksID, type)
        return Playlist(
            playlistId = entity.playlistId,
            playlistName = entity.playlistName,
            playlistDescription = entity.playlistDescription,
            path = entity.path,
            tracksID = trackIds,
            tracksCount = entity.tracksCount
        )
    }

}
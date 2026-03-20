package com.example.playlistmaker.domain.models

import java.io.Serializable

data class Playlist(
    val playlistId: Int = 0,
    val playlistName: String,
    val playlistDescription: String?,
    val path: String?,
    val tracksID: List<Int> = emptyList(),
    val tracksCount: Int = 0
) : Serializable

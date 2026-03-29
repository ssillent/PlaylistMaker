package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: Long): PlaylistTrackEntity?

    @Query("SELECT * FROM playlist_tracks_table WHERE trackId IN (:trackIds)")
    fun getTracksByIds(trackIds: List<Long>): Flow<List<PlaylistTrackEntity>>

    @Query("DELETE FROM playlist_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Long)

}
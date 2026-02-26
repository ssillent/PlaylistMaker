package com.example.playlistmaker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteTrack(track: TrackEntity)

    @Delete
    suspend fun deleteFavoriteTrack(track: TrackEntity)

    @Query("SELECT * FROM track_table ORDER BY addedTimestamp DESC")
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT trackId FROM track_table")
    suspend fun  getAllFavoriteTracksId(): List<Long>

    @Query("SELECT EXISTS(SELECT 1 FROM track_table WHERE trackId = :trackId)")
    suspend fun isTrackFavorite(trackId: Long): Boolean

}
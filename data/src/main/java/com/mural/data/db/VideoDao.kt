package com.mural.data.db

import androidx.room.*
import com.mural.data.dto.VideoData

@Dao
interface VideoDao {

    @Transaction
    suspend fun updateVideo(videoData: VideoData) {
        insertVideo(videoData)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(videoData: VideoData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videoData: List<VideoData>)

    @Query("DELETE FROM videos")
    suspend fun deleteAllVideo()

}
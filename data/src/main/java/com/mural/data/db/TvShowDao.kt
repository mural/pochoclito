package com.mural.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.mural.data.dto.TvShowData
import com.mural.data.dto.TvShowWithVideos
import kotlinx.coroutines.flow.Flow

@Dao
interface TvShowDao {

    @Transaction
    suspend fun updateTvShow(tvShowData: TvShowData) {
        insertTvShow(tvShowData)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShow(tvShowData: TvShowData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTvShows(tvShowData: List<TvShowData>)

    @Query("DELETE FROM tv_show_table")
    suspend fun deleteAllTvShow()

    @Query("SELECT * FROM tv_show_table WHERE tvId = :id")
    fun getTvShow(id: Long): Flow<TvShowData>

    @Transaction
    @Query("SELECT * FROM tv_show_table WHERE tvId = :id")
    fun getTvShowAndVideos(id: Long): Flow<TvShowWithVideos>

    @Query("SELECT * FROM tv_show_table ORDER by popularity DESC")
    fun getTvShows(): PagingSource<Int, TvShowData>

    @Query("SELECT * FROM tv_show_table WHERE name LIKE '%' || :query || '%'")
    fun getTvShows(query: String): PagingSource<Int, TvShowData>

}
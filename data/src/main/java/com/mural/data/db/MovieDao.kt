package com.mural.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.mural.data.dto.MovieWithVideos
import com.mural.data.dto.MovieData
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Transaction
    suspend fun updateMovie(movieData: MovieData) {
        insertMovie(movieData)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movieData: MovieData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movieData: List<MovieData>)

    @Query("DELETE FROM movie_table")
    suspend fun deleteAllMovies()

    @Query("SELECT * FROM movie_table WHERE movieId = :id")
    fun getMovie(id: Long): Flow<MovieData>

    @Transaction
    @Query("SELECT * FROM movie_table WHERE movieId = :id")
    fun getMovieAndVideos(id: Long): Flow<MovieWithVideos>

    @Query("SELECT * FROM movie_table ORDER by popularity DESC")
    fun getMovies(): PagingSource<Int, MovieData>

    @Query("SELECT * FROM movie_table WHERE title LIKE '%' || :query || '%'")
    fun getMovies(query: String): PagingSource<Int, MovieData>

}
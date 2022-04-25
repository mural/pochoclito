package com.mural.data.repository

import com.mural.data.api.RemoteApiService
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val remoteApiService: RemoteApiService) {
    suspend fun getTopMovies(page: Int) =
        remoteApiService.getTopMovies(page)

    suspend fun getMovieDetail(movieId: Long) =
        remoteApiService.getMovieDetail(id = movieId)

    suspend fun getMovieVideos(movieId: Long) =
        remoteApiService.getMovieVideos(id = movieId)

    suspend fun searchMovies(query: String) =
        remoteApiService.searchMovie(query = query)


    suspend fun getTopTvShows(page: Int) =
        remoteApiService.getTopTvShows(page)

    suspend fun getTvShowDetail(tvId: Long) =
        remoteApiService.getTvShowDetail(id = tvId)

    suspend fun getTvShowVideos(tvId: Long) =
        remoteApiService.getTvShowVideos(id = tvId)

    suspend fun searchTvShows(query: String) =
        remoteApiService.searchTvShow(query = query)
}
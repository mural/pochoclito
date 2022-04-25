package com.mural.data.api

import com.mural.data.Constants
import com.mural.data.dto.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RemoteApiService {

    @GET(Constants.POPULAR_MOVIES_URL)
    suspend fun getTopMovies(@Query("page") page: Int): Response<MovieResponse>

    @GET(Constants.MOVIE_DETAIL_URL)
    suspend fun getMovieDetail(@Path(Constants.MOVIE_ID) id: Long): Response<MovieData>

    @GET(Constants.MOVIE_VIDEOS_URL)
    suspend fun getMovieVideos(@Path(Constants.MOVIE_ID) id: Long): Response<VideoResponse>

    @GET(Constants.SEARCH_MOVIE)
    suspend fun searchMovie(@Query("query") query: String): Response<MovieResponse>


    @GET(Constants.POPULAR_TV_SHOWS_URL)
    suspend fun getTopTvShows(@Query("page") page: Int): Response<TvShowResponse>

    @GET(Constants.TV_SHOW_DETAIL_URL)
    suspend fun getTvShowDetail(@Path(Constants.MOVIE_ID) id: Long): Response<TvShowData>

    @GET(Constants.TV_SHOW_VIDEOS_URL)
    suspend fun getTvShowVideos(@Path(Constants.MOVIE_ID) id: Long): Response<VideoResponse>

    @GET(Constants.SEARCH_TV_SHOW)
    suspend fun searchTvShow(@Query("query") query: String): Response<TvShowResponse>

}
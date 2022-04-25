package com.mural.data.repository

import android.util.Log
import com.mural.data.db.MovieDao
import com.mural.data.db.VideoDao
import com.mural.data.dto.MovieData
import com.mural.data.dto.MovieWithVideos
import com.mural.data.module.IoDispatcher
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ActivityRetainedScoped
class MovieRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val movieDao: MovieDao,
    private val videoDao: VideoDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : BaseApiResponse() {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getMovie(movieId: Long): Flow<NetworkResult<MovieWithVideos>> {
        Log.d("Repository", "Call cached")
        val cachedMovie = movieDao.getMovie(movieId).first()
        val cachedFlow = flow<NetworkResult<MovieWithVideos>> {
            movieDao.getMovieAndVideos(movieId).collect {
                Log.d("Repository", "Emitting cached result")
                emit(NetworkResult.Cached(it))
            }
        }

        val networkFlow = flow {
            Log.d("Repository", "Call network")
            val resultMovies = safeApiCallWithData(data = cachedMovie) {
                remoteDataSource.getMovieDetail(movieId)
            }
            val resultVideos = safeApiCall() {
                remoteDataSource.getMovieVideos(movieId)
            }

            val resultMoviesWithVideo: NetworkResult<MovieWithVideos> =
                if (resultMovies is NetworkResult.Success && resultVideos is NetworkResult.Success) {
                    val movieWithVideos = MovieWithVideos(
                        movieData = resultMovies.data ?: MovieData(0L),
                        videoData = resultVideos.data?.results ?: emptyList()
                    )
                    NetworkResult.Success(movieWithVideos)
                } else {
                    val movieWithVideos = MovieWithVideos(
                        movieData = resultMovies.data!!,
                        videoData = resultVideos.data?.results ?: emptyList()
                    )
                    NetworkResult.Error(resultMovies.message ?: "", movieWithVideos)
                }

            Log.d("Repository", "Emitting network result")
            emit(resultMoviesWithVideo)

            if (resultMovies is NetworkResult.Success) {
                Log.d("Repository", "NetworkResult.Success")
                resultMovies.data?.run {
                    Log.d("Repository", "Saving movies network success into database")
                    movieDao.updateMovie(this)

                    if (resultVideos is NetworkResult.Success) {
                        resultVideos.data?.run {
                            Log.d("Repository", "Saving videoData network success into database")
                            this.results.forEach {
                                it.movieId = movieId
                            }
                            videoDao.insertVideos(this.results)
                        }
                    }
                }
            } else {
                Log.d("Repository", "NetworkResult ${resultMovies.message}")
            }
        }.flowOn(dispatcher)

        return merge(cachedFlow, networkFlow)
    }

}
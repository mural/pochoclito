package com.mural.data.repository

import android.util.Log
import com.mural.data.db.TvShowDao
import com.mural.data.db.VideoDao
import com.mural.data.dto.TvShowData
import com.mural.data.dto.TvShowWithVideos
import com.mural.data.module.IoDispatcher
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@ActivityRetainedScoped
class TvShowRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val TvShowDao: TvShowDao,
    private val videoDao: VideoDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : BaseApiResponse() {

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getTvShow(tvShowId: Long): Flow<NetworkResult<TvShowWithVideos>> {
        Log.d("Repository", "Call cached")
        val cachedTvShow = TvShowDao.getTvShow(tvShowId).first()
        val cachedFlow = flow<NetworkResult<TvShowWithVideos>> {
            TvShowDao.getTvShowAndVideos(tvShowId).collect {
                Log.d("Repository", "Emitting cached result")
                emit(NetworkResult.Cached(it))
            }
        }

        val networkFlow = flow {
            Log.d("Repository", "Call network")
            val resultTvShows = safeApiCallWithData(data = cachedTvShow) {
                remoteDataSource.getTvShowDetail(tvShowId)
            }
            val resultVideos = safeApiCall() {
                remoteDataSource.getTvShowVideos(tvShowId)
            }

            val resultTvShowsWithVideo: NetworkResult<TvShowWithVideos> =
                if (resultTvShows is NetworkResult.Success && resultVideos is NetworkResult.Success) {
                    val tvShowWithVideos = TvShowWithVideos(
                        tvShowData = resultTvShows.data ?: TvShowData(0L),
                        videoData = resultVideos.data?.results ?: emptyList()
                    )
                    NetworkResult.Success(tvShowWithVideos)
                } else {
                    val tvShowWithVideos = TvShowWithVideos(
                        tvShowData = resultTvShows.data!!,
                        videoData = resultVideos.data?.results ?: emptyList()
                    )
                    NetworkResult.Error(resultTvShows.message ?: "", tvShowWithVideos)
                }

            Log.d("Repository", "Emitting network result")
            emit(resultTvShowsWithVideo)

            if (resultTvShows is NetworkResult.Success) {
                Log.d("Repository", "NetworkResult.Success")
                resultTvShows.data?.run {
                    Log.d("Repository", "Saving TvShows network success into database")
                    TvShowDao.updateTvShow(this)

                    if (resultVideos is NetworkResult.Success) {
                        resultVideos.data?.run {
                            Log.d("Repository", "Saving videoData network success into database")
                            this.results.forEach {
                                it.tvShowId = tvShowId
                            }
                            videoDao.insertVideos(this.results)
                        }
                    }
                }
            } else {
                Log.d("Repository", "NetworkResult ${resultTvShows.message}")
            }
        }.flowOn(dispatcher)

        return merge(cachedFlow, networkFlow)
    }

}
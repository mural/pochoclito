package com.mural.data.source

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mural.data.db.PochoclitoDatabase
import com.mural.data.dto.TvShowData
import com.mural.data.repository.RemoteDataSource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class TvShowsRemoteMediator @Inject constructor
    (
    private val database: PochoclitoDatabase,
    private val remoteDataSource: RemoteDataSource
) : RemoteMediator<Int, TvShowData>() {
    val tvShowDao = database.tvShowDao()

    var lastPageRetrieved = 1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TvShowData>
    ): MediatorResult {
        Log.d("TvShowsRemoteMediator", "onLoad")
        return try {
            when (loadType) {
                LoadType.REFRESH -> {
                    lastPageRetrieved = 1
                }
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    lastPageRetrieved++
                }
            }
            val response = remoteDataSource.getTopTvShows(
                page = lastPageRetrieved
            )

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    tvShowDao.deleteAllTvShow()
                }
                // Insert new TvShows into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                tvShowDao.insertTvShows(response.body()!!.results)
            }

            MediatorResult.Success(
                endOfPaginationReached = response.body()!!.page == response.body()!!.totalPages
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
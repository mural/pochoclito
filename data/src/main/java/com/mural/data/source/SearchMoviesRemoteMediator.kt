package com.mural.data.source

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.mural.data.db.PochoclitoDatabase
import com.mural.data.dto.MovieData
import com.mural.data.repository.RemoteDataSource
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class SearchMoviesRemoteMediator @Inject constructor
    (
    private val database: PochoclitoDatabase,
    private val remoteDataSource: RemoteDataSource,
    var searchQuery: String = "",
) : RemoteMediator<Int, MovieData>() {
    val movieDao = database.movieDao()
    var lastPageRetrieved = 1

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieData>
    ): MediatorResult {
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
            val response = remoteDataSource.searchMovies(
                query = searchQuery
            )

            database.withTransaction {
                // Insert new movies into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                response.body()?.let {
                    movieDao.insertMovies(it.results)
                }
            }

            var paginationEnd = true
            response.body()?.let {
                paginationEnd = it.page == it.totalPages
            }
            MediatorResult.Success(
                endOfPaginationReached = paginationEnd
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
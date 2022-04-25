package com.mural.pochoclito.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.mural.data.db.MovieDao
import com.mural.data.dto.toDomain
import com.mural.data.repository.MovieRepository
import com.mural.data.repository.NetworkResult
import com.mural.data.source.MoviesRemoteMediator
import com.mural.data.source.SearchMoviesRemoteMediator
import com.mural.domain.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class MovieViewModel @Inject constructor
    (
    private val movieDao: MovieDao,
    private val movieRepository: MovieRepository,
    movieRemoteMediator: MoviesRemoteMediator,
    private val searchMovieRemoteMediator: SearchMoviesRemoteMediator,
) : ViewModel() {
    private val _response: MutableLiveData<NetworkResult<Movie>> = MutableLiveData()
    val response: LiveData<NetworkResult<Movie>> = _response

    @OptIn(ExperimentalPagingApi::class)
    val movies: Flow<PagingData<Movie>> =
        Pager(PagingConfig(pageSize = 10), remoteMediator = movieRemoteMediator) {
            movieDao.getMovies()
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { movieData ->
                movieData.toDomain()
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    fun searchMovieByName(query: String): Flow<PagingData<Movie>> {
        return if (query.length >= 3) {
            searchMovieRemoteMediator.searchQuery = query
            Pager(PagingConfig(pageSize = 10), remoteMediator = searchMovieRemoteMediator) {
                movieDao.getMovies(query)
            }.flow.cachedIn(viewModelScope).map { pagingData ->
                pagingData.map { movieData ->
                    movieData.toDomain()
                }
            }
        } else {
            flow { }
        }
    }

    fun getMovieDetails(id: Long) = viewModelScope.launch {
        movieRepository.getMovie(id).collectLatest {
            _response.value = when (it) {
                is NetworkResult.Loading -> NetworkResult.Loading()
                is NetworkResult.Cached -> NetworkResult.Cached(it.data?.toDomain())
                is NetworkResult.Success -> NetworkResult.Success(it.data?.toDomain())
                is NetworkResult.Error -> NetworkResult.Error(it.message, it.data?.toDomain())
            }
        }
    }
}
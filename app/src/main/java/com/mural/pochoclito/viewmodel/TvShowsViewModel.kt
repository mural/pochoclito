package com.mural.pochoclito.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.mural.data.db.TvShowDao
import com.mural.data.dto.toDomain
import com.mural.data.repository.NetworkResult
import com.mural.data.repository.TvShowRepository
import com.mural.data.source.SearchTvShowsRemoteMediator
import com.mural.data.source.TvShowsRemoteMediator
import com.mural.domain.TvShow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvShowsViewModel @Inject constructor
    (
    private val tvShowDao: TvShowDao,
    private val tvShowRepository: TvShowRepository,
    tvShowsRemoteMediator: TvShowsRemoteMediator,
    private val searchTvShowsRemoteMediator: SearchTvShowsRemoteMediator,
) : ViewModel() {
    private val _response: MutableLiveData<NetworkResult<TvShow>> = MutableLiveData()
    val response: LiveData<NetworkResult<TvShow>> = _response

    @OptIn(ExperimentalPagingApi::class)
    val tvShowsData: Flow<PagingData<TvShow>> =
        Pager(PagingConfig(pageSize = 10), remoteMediator = tvShowsRemoteMediator) {
            tvShowDao.getTvShows()
        }.flow.cachedIn(viewModelScope).map { pagingData ->
            pagingData.map { tvData ->
                tvData.toDomain()
            }
        }

    @OptIn(ExperimentalPagingApi::class)
    fun searchTvShowByName(query: String): Flow<PagingData<TvShow>> {
        return if (query.length >= 3) {
            searchTvShowsRemoteMediator.searchQuery = query
            Pager(PagingConfig(pageSize = 10), remoteMediator = searchTvShowsRemoteMediator) {
                tvShowDao.getTvShows(query)
            }.flow.cachedIn(viewModelScope).map { pagingData ->
                pagingData.map { tvShowData ->
                    tvShowData.toDomain()
                }
            }
        } else {
            flow { }
        }
    }

    fun getTvShowDetails(id: Long) = viewModelScope.launch {
        tvShowRepository.getTvShow(id).collectLatest {
            _response.value = when (it) {
                is NetworkResult.Loading -> NetworkResult.Loading()
                is NetworkResult.Cached -> NetworkResult.Cached(it.data?.toDomain())
                is NetworkResult.Success -> NetworkResult.Success(it.data?.toDomain())
                is NetworkResult.Error -> NetworkResult.Error(it.message, it.data?.toDomain())
            }
        }
    }
}
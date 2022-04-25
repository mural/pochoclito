package com.mural.pochoclito

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mural.data.db.MovieDao
import com.mural.data.dto.*
import com.mural.data.repository.MovieRepository
import com.mural.data.repository.NetworkResult
import com.mural.data.repository.RemoteDataSource
import com.mural.data.source.MoviesRemoteMediator
import com.mural.data.source.SearchMoviesRemoteMediator
import com.mural.domain.Movie
import com.mural.pochoclito.helpers.CoroutineTestRule
import com.mural.pochoclito.helpers.LifeCycleTestOwner
import com.mural.pochoclito.helpers.TestableObserver
import com.mural.pochoclito.viewmodel.MovieViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class AppUnitTest {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieDao: MovieDao
    private lateinit var movieRepository: MovieRepository
    private lateinit var movieRemoteMediator: MoviesRemoteMediator
    private lateinit var searchMovieRemoteMediator: SearchMoviesRemoteMediator
    private lateinit var remoteDataSource: RemoteDataSource

    private lateinit var lifeCycleTestOwner: LifeCycleTestOwner
    private lateinit var movieObserver: Observer<NetworkResult<Movie>>
    private lateinit var baseMovieResponse: MovieResponse
    private lateinit var cachedMovieWithVideos: MovieWithVideos
    private lateinit var networkMovieWithVideos: MovieWithVideos

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Before
    fun setUp() {
        lifeCycleTestOwner = LifeCycleTestOwner()
        lifeCycleTestOwner.onCreate()
        movieObserver = mockk()

        movieDao = mockk()
        movieRepository = mockk()
        movieRemoteMediator = mockk()
        searchMovieRemoteMediator = mockk()
        remoteDataSource = mockk()
        movieViewModel = MovieViewModel(
            movieDao,
            movieRepository,
            movieRemoteMediator,
            searchMovieRemoteMediator
        )
        movieViewModel.response.observe(lifeCycleTestOwner, movieObserver)

        val movie1Cached = MovieData(
            1L,
            title = "MovieData 1 cached"
        )
        val movie2Cached = MovieData(
            2L,
            title = "MovieData 2 cached"
        )
        baseMovieResponse = MovieResponse(
            page = 1,
            totalPages = 10,
            totalResults = 500,
            results = listOf(movie1Cached, movie2Cached)
        )
        cachedMovieWithVideos =
            MovieWithVideos(
                movie1Cached,
                listOf(VideoData("1", 1L, 1L, "VideoData 1 de movieData 1 cached"))
            )

        val movie1NetworkSuccess = MovieData(1L, title = "MovieData 1 network", budget = 100100)
        networkMovieWithVideos = MovieWithVideos(
            movie1NetworkSuccess,
            listOf(VideoData("1", 1L, 1L, "VideoData 1 de movieData 1 network"))
        )
    }

    @Test
    fun `check if test are running ok`() {
        assertEquals(4, 2 + 2)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Given getMovieDetails is called When no errors occurs Then Loading, Cache and Success states should be retrieved`() =
        coroutinesTestRule.testDispatcher.run() {
            val loadingResult = NetworkResult.Loading<MovieWithVideos>()
            val networkResult = NetworkResult.Success(cachedMovieWithVideos)

            val movieFlowLoading: Flow<NetworkResult<MovieWithVideos>> =
                flow { emit(loadingResult) }
            val movieFlowNetworkSuccess: Flow<NetworkResult<MovieWithVideos>> =
                flow { emit(networkResult) }

            val stateObserver = TestableObserver<NetworkResult<Movie>>()
            movieViewModel.response.apply {
                observeForever(stateObserver)
            }

            //Given a movieData
            coEvery { movieRepository.getMovie(1L) } returns merge(
                movieFlowLoading,
                movieFlowNetworkSuccess
            )

            // When getting movieData details
            movieViewModel.getMovieDetails(1L)

            stateObserver.assertAllEmitted(
                listOf(
                    loadingResult,
                    networkResult
                ).map { it ->
                    when (it) {
                        is NetworkResult.Loading -> NetworkResult.Loading()
                        is NetworkResult.Cached -> NetworkResult.Cached(it.data?.toDomain())
                        is NetworkResult.Success -> NetworkResult.Success(it.data?.toDomain())
                        is NetworkResult.Error -> NetworkResult.Error(
                            it.message,
                            it.data?.toDomain()
                        )
                    }
                }
            )
        }

    @After
    fun tearDown() {
        lifeCycleTestOwner.onDestroy()
    }


}


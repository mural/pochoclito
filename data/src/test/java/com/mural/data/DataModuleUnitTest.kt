package com.mural.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mural.data.api.RemoteApiService
import com.mural.data.db.MovieDao
import com.mural.data.db.VideoDao
import com.mural.data.dto.MovieResponse
import com.mural.data.repository.MovieRepository
import com.mural.data.repository.NetworkResult
import com.mural.data.repository.RemoteDataSource
import com.mural.data.dto.MovieData
import com.mural.data.helpers.CoroutineTestRule
import com.mural.data.helpers.LifeCycleTestOwner
import com.mural.data.helpers.MockResponseFileReader
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DataModuleUnitTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var okHttpClient: OkHttpClient
    private lateinit var remoteApiService: RemoteApiService


    private lateinit var repository: MovieRepository
    private lateinit var remoteDataSource: RemoteDataSource
    private lateinit var movieDao: MovieDao
    private lateinit var videoDao: VideoDao

    private lateinit var lifeCycleTestOwner: LifeCycleTestOwner
    private lateinit var movieDataObserver: Observer<NetworkResult<MovieData>>

    private lateinit var baseMovieResponse: MovieResponse

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        lifeCycleTestOwner = LifeCycleTestOwner()
        lifeCycleTestOwner.onCreate()
        movieDataObserver = mockk()

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .build()
        remoteApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RemoteApiService::class.java)
        remoteDataSource = RemoteDataSource(remoteApiService)
        movieDao = mockk()
        videoDao = mockk()

        repository = MovieRepository(
            remoteDataSource = remoteDataSource,
            movieDao = movieDao,
            videoDao = videoDao,
            dispatcher = coroutinesTestRule.testDispatcher
        )
    }

    @Test
    fun `read sample success json file`() {
        val reader = MockResponseFileReader("movies_success_response.json")
        assertNotNull(reader.content)
    }

    @Test
    fun `should fetch movies correctly given 200 response`() {
        mockWebServer.enqueueResponse("movies_success_response.json", 200)

        runBlocking {
            val actual = remoteApiService.getTopMovies(page = 1)
            val expected = MovieData(634649L)

            assertEquals(expected, actual.body()?.results?.get(0) ?: MovieData(0L))
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun MockWebServer.enqueueResponse(fileName: String, code: Int) {
        val reader = MockResponseFileReader(fileName)
        reader.content?.let {
            enqueue(
                MockResponse()
                    .setResponseCode(code)
                    .setBody(it)
            )
        }
    }
}


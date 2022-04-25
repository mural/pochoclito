package com.mural.data.db

import android.content.Context
import androidx.paging.PagingSource
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.mural.data.dto.MovieData
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PochoclitoDatabaseTest : TestCase() {
    private lateinit var db: PochoclitoDatabase
    private lateinit var dao: MovieDao

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PochoclitoDatabase::class.java).build()
        dao = db.movieDao()
    }

    @Test
    fun test_insert_movie_and_retrieve_it() = runBlocking {
        val movie = MovieData(1L)
        dao.insertMovie(movie)
        val movies = dao.getMovie(1L)
        assertEquals(movie, movies.first())
    }

    @Test
    fun test_insert_movie_and_retrieve_another_error() = runBlocking {
        val movie = MovieData(1L)
        dao.insertMovie(movie)
        val movies = dao.getMovie(2L)
        assertNotSame(movie, movies.first())
    }

    @Test
    fun test_insert_movie_and_retrieve_all_contains_it() = runBlocking {
        val movie = MovieData(1L)
        dao.insertMovie(movie)
        val moviesActual =
            dao.getMovies()
                .load(PagingSource.LoadParams.Refresh(MovieData(1L).hashCode(), 5, false))
        val moviesData = (moviesActual as? PagingSource.LoadResult.Page)?.data
        assertThat(moviesData?.contains(movie) ?: false).isTrue()

    }

    @Test
    fun test_insert_movie_and_retrieve_all_not_contains_another() = runBlocking {
        val movie = MovieData(1L)
        val movie2 = MovieData(2L)
        dao.insertMovie(movie)
        val moviesActual =
            dao.getMovies()
                .load(PagingSource.LoadParams.Refresh(MovieData(1L).hashCode(), 5, false))
        val moviesData = (moviesActual as? PagingSource.LoadResult.Page)?.data
        assertThat(moviesData?.contains(movie2) ?: false).isFalse()
    }

    @After
    fun closeDb() {
        db.close()
    }
}
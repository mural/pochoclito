package com.mural.data.module

import android.content.Context
import com.mural.data.db.MovieDao
import com.mural.data.db.PochoclitoDatabase
import com.mural.data.db.TvShowDao
import com.mural.data.db.VideoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PochoclitoDatabase =
        PochoclitoDatabase.create(context)

    @Provides
    fun provideMovieDao(database: PochoclitoDatabase): MovieDao {
        return database.movieDao()
    }


    @Provides
    fun provideTvShowDao(database: PochoclitoDatabase): TvShowDao {
        return database.tvShowDao()
    }

    @Provides
    fun provideVideoDao(database: PochoclitoDatabase): VideoDao {
        return database.videoDao()
    }
}
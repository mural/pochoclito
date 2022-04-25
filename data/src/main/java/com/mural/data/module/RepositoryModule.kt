package com.mural.data.module

import com.mural.data.db.PochoclitoDatabase
import com.mural.data.repository.RemoteDataSource
import com.mural.data.source.MoviesRemoteMediator
import com.mural.data.source.SearchMoviesRemoteMediator
import com.mural.data.source.SearchTvShowsRemoteMediator
import com.mural.data.source.TvShowsRemoteMediator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieMediator(
        database: PochoclitoDatabase,
        remoteDataSource: RemoteDataSource
    ): MoviesRemoteMediator =
        MoviesRemoteMediator(database, remoteDataSource)

    @Provides
    @Singleton
    fun provideSearchMovieMediator(
        database: PochoclitoDatabase,
        remoteDataSource: RemoteDataSource,
    ): SearchMoviesRemoteMediator =
        SearchMoviesRemoteMediator(database, remoteDataSource)

    @Provides
    @Singleton
    fun provideTvMediator(
        database: PochoclitoDatabase,
        remoteDataSource: RemoteDataSource
    ): TvShowsRemoteMediator =
        TvShowsRemoteMediator(database, remoteDataSource)

    @Provides
    @Singleton
    fun provideSearchTvMediator(
        database: PochoclitoDatabase,
        remoteDataSource: RemoteDataSource,
    ): SearchTvShowsRemoteMediator =
        SearchTvShowsRemoteMediator(database, remoteDataSource)
}
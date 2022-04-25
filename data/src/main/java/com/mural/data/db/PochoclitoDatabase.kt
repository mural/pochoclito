package com.mural.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mural.data.dto.MovieData
import com.mural.data.dto.TvShowData
import com.mural.data.dto.VideoData

private const val DB_NAME = "pochoclito_database"

@Database(entities = [(MovieData::class), (TvShowData::class), (VideoData::class)], version = 20)
abstract class PochoclitoDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun tvShowDao(): TvShowDao
    abstract fun videoDao(): VideoDao

    companion object {
        fun create(context: Context): PochoclitoDatabase {

            return Room.databaseBuilder(
                context,
                PochoclitoDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
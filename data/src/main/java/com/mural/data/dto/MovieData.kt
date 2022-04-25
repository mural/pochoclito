package com.mural.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.mural.domain.Movie

@Entity(tableName = "movie_table")
data class MovieData(
    @PrimaryKey @SerializedName("id") val movieId: Long,
    @SerializedName("title") val title: String? = "",
    @SerializedName("backdrop_path") val backdropPath: String? = "",
    @SerializedName("popularity") val popularity: Double? = 0.0,
    @SerializedName("vote_average") val voteAverage: Double? = 0.0,
    @SerializedName("budget") val budget: Long? = 0L,
    @SerializedName("release_date") val releaseDate: String? = ""
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is MovieData -> {
                this.movieId == other.movieId
            }
            else -> false
        }
    }
}

fun MovieData.toDomain(): Movie {
    return toDomain(emptyList())
}

fun MovieData.toDomain(videos: List<VideoData>): Movie {
    return Movie(
        movieId = movieId,
        title = title,
        backdropPath = backdropPath,
        popularity = popularity,
        voteAverage = voteAverage,
        budget = budget,
        releaseDate = releaseDate,
        videos = videos.map { videoData -> videoData.toDomain(this) }
    )
}
package com.mural.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.mural.domain.TvShow

@Entity(tableName = "tv_show_table")
data class TvShowData(
    @PrimaryKey @SerializedName("id") val tvId: Long,
    @SerializedName("name") val name: String? = "",
    @SerializedName("overview") val overview: String? = "",
    @SerializedName("backdrop_path") val backdropPath: String? = "",
    @SerializedName("popularity") val popularity: Double? = 0.0,
    @SerializedName("vote_average") val voteAverage: Double? = 0.0,
    @SerializedName("in_production") val inProduction: Boolean? = false,
    @SerializedName("first_air_date") val firstAirDate: String? = ""
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TvShowData -> {
                this.tvId == other.tvId
            }
            else -> false
        }
    }
}

fun TvShowData.toDomain(): TvShow {
    return toDomain(emptyList())
}

fun TvShowData.toDomain(videos: List<VideoData>): TvShow {
    return TvShow(
        tvId = tvId,
        name = name,
        overview = overview,
        backdropPath = backdropPath,
        popularity = popularity,
        voteAverage = voteAverage,
        inProduction = inProduction,
        firstAirDate = firstAirDate,
        videos = videos.map { videoData -> videoData.toDomain(this) }
    )
}
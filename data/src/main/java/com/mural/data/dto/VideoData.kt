package com.mural.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.mural.domain.Movie
import com.mural.domain.TvShow
import com.mural.domain.Video

@Entity(tableName = "videos")
data class VideoData(
    @PrimaryKey @SerializedName("id") val videoId: String,
    var movieId: Long? = 0L,
    var tvShowId: Long? = 0L,
    val name: String? = "",
    val site: String? = "",
    val key: String? = ""
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is VideoData -> {
                this.videoId == other.videoId
            }
            else -> false
        }
    }
}

fun VideoData.toDomain(parent: Any): Video {
    return Video(
        videoId = videoId,
        ownerId = if (parent is Movie) parent.movieId else (parent as? TvShow)?.tvId ?: 0L,
        name = name,
        site = site,
        key = key
    )
}
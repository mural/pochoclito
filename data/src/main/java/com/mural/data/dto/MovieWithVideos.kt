package com.mural.data.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.mural.domain.Movie

class MovieWithVideos(
    @Embedded var movieData: MovieData,
    @Relation(
        parentColumn = "movieId",
        entityColumn = "movieId"
    )
    var videoData: List<VideoData>,
)

fun MovieWithVideos.toDomain(): Movie {
    return movieData.toDomain(videoData)
}
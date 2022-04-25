package com.mural.data.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.mural.domain.TvShow

class TvShowWithVideos(
    @Embedded var tvShowData: TvShowData,
    @Relation(
        parentColumn = "tvId",
        entityColumn = "tvShowId"
    )
    var videoData: List<VideoData>,
)

fun TvShowWithVideos.toDomain(): TvShow {
    return tvShowData.toDomain(videoData)
}
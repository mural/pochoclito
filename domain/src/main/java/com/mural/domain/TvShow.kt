package com.mural.domain

data class TvShow(
    val tvId: Long,
    val name: String?,
    val backdropPath: String?,
    val popularity: Double?,
    val voteAverage: Double?,
    val inProduction: Boolean? = false,
    val firstAirDate: String? = "",
    val videos: List<Video> = listOf(),
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is TvShow -> {
                this.tvId == other.tvId
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        return tvId.hashCode()
    }
}
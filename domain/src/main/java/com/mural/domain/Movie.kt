package com.mural.domain

data class Movie(
    val movieId: Long,
    val title: String? = "",
    val backdropPath: String? = "",
    val popularity: Double? = 0.0,
    val voteAverage: Double? = 0.0,
    val budget: Long? = 0L,
    val releaseDate: String? = "",
    val videos: List<Video> = listOf(),
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Movie -> {
                this.movieId == other.movieId
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        return movieId.hashCode()
    }
}
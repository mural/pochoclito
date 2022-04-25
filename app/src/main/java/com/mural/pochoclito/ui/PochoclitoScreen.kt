package com.mural.pochoclito.ui

import com.mural.pochoclito.R

enum class PochoclitoScreen(
    val iconResId: Int,
) {
    Movies(
        iconResId = R.drawable.ic_movies,
    ),
    TvShows(
        iconResId = R.drawable.ic_tv_shows,
    ),
    Details(
        iconResId = 0,
    );

    override fun toString(): String {
        return when (this) {
            Movies -> "Movies"
            TvShows -> "Tv Shows"
            Details -> "Details"
        }
    }

    companion object {
        fun fromRoute(route: String?): PochoclitoScreen =
            when (route?.substringBefore("/")) {
                Movies.name -> Movies
                TvShows.name -> TvShows
                Details.name -> Details
                null -> Movies
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}
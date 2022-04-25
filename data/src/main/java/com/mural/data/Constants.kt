package com.mural.data

class Constants {

    companion object {
        private const val API_KEY = BuildConfig.API_KEY

        const val BASE_URL = "https://api.themoviedb.org/3/"

        const val POPULAR_MOVIES_URL =
            "discover/movie?sort_by=popularity.desc&api_key=$API_KEY"

        const val POPULAR_TV_SHOWS_URL =
            "discover/tv?sort_by=popularity.desc&api_key=$API_KEY"

        const val MOVIE_ID = "movieId"
        const val MOVIE_DETAIL_URL =
            "movie/{$MOVIE_ID}?api_key=$API_KEY"
        const val MOVIE_VIDEOS_URL =
            "movie/{$MOVIE_ID}/videos?api_key=$API_KEY"
        const val SEARCH_MOVIE =
            "search/movie?api_key=$API_KEY"

        const val TV_SHOW_ID = "tvShowId"
        const val TV_SHOW_DETAIL_URL =
            "tv/{$TV_SHOW_ID}?api_key=$API_KEY"
        const val TV_SHOW_VIDEOS_URL =
            "tv/{${TV_SHOW_ID}/videos?api_key=$API_KEY"
        const val SEARCH_TV_SHOW =
            "search/tv?api_key=$API_KEY"
    }

}
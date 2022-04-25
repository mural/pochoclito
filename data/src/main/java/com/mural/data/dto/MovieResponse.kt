package com.mural.data.dto

import com.google.gson.annotations.SerializedName

class MovieResponse(
    var page: Int,
    @SerializedName("total_pages") var totalPages: Int,
    @SerializedName("total_results") var totalResults: Int,
    var results: List<MovieData>,
)
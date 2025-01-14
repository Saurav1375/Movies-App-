package com.example.moviesapp.data.remote.dto.moviedto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MovieDto(
    val dates: Dates?, // Made nullable since not all endpoints have dates
    val page: Int,
    val results: List<Result>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)

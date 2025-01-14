package com.example.moviesapp.data.remote.dto.moviedetailsdto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Genre(
    val id: Int?,
    val name: String?
)


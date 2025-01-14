package com.example.moviesapp.data.remote.dto.creditsDto


import com.squareup.moshi.Json

data class CreditsDto(
    val cast: List<Cast?>?,
    val id: Int?
)
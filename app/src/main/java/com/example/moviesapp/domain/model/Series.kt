package com.example.moviesapp.domain.model


data class Series(
    val id: Int,
    val name: String,
    val firstAirDate: String,
    val posterPath: String,
    val type: String,
)
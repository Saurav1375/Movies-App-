package com.example.moviesapp.domain.model

data class SearchMovie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
)
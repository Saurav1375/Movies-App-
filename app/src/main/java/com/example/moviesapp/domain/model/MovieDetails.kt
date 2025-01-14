package com.example.moviesapp.domain.model

data class MovieDetails(
    val id: Int,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val overview: String,
    val isAdult: Boolean,
    val releaseDate: String,
    val runtime: Int,
    val status: String,
    val tagline: String,
    val popularity: Double,
    val originalLanguage: String,
    val originalTitle: String,
    val genres: List<String>,
    val voteAverage: Double,
    val voteCount: Int
)
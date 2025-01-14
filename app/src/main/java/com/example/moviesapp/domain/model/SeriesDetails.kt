package com.example.moviesapp.domain.model

import androidx.room.Entity

@Entity(tableName = "seriesDetails_table")
data class SeriesDetails(
    val id: Int,
    val title: String,
    val posterPath: String,
    val backdropPath: String,
    val overview: String,
    val isAdult: Boolean,
    val firstAirDate: String,
    val status: String,
    val tagline: String,
    val popularity: Double,
    val originalLanguage: String,
    val originalName: String,
    val genres: List<String>,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val voteAverage: Double,
    val voteCount: Int
)
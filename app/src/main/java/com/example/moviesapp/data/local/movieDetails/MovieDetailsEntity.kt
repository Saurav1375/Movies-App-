package com.example.moviesapp.data.local.movieDetails

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movieDetails_table")
data class MovieDetailsEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
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
    val genres: String,
    val voteAverage: Double,
    val voteCount: Int
)
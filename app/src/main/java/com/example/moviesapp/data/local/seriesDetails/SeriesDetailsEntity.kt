package com.example.moviesapp.data.local.seriesDetails

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seriesDetails_table")
data class SeriesDetailsEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val name: String,
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
    val genres: String,
    val numberOfEpisodes: Int,
    val numberOfSeasons: Int,
    val voteAverage: Double,
    val voteCount: Int

    )
package com.example.moviesapp.data.local.searchSeries

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_series")
data class SearchSeriesEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    val page: Int,
    val query: String
)
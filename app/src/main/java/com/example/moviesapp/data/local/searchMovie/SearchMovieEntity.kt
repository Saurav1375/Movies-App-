package com.example.moviesapp.data.local.searchMovie

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_movies")
data class SearchMovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    val page: Int,
    val query: String
)
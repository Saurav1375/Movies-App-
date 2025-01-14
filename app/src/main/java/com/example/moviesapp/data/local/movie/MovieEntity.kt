package com.example.moviesapp.data.local.movie

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "movie_table")
data class MovieEntity (
    @PrimaryKey(autoGenerate = false) val id : Int,
    val title : String,
    val releaseDate : String,
    val posterPath : String,
    val type : String,
)
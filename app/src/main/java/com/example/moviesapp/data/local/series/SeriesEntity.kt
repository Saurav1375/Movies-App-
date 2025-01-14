package com.example.moviesapp.data.local.series

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "series_table")
data class SeriesEntity(
    @PrimaryKey(autoGenerate = false) val id: Int,
    val  name: String,
    val firstAirDate: String,
    val posterPath: String,
    val type: String,
)
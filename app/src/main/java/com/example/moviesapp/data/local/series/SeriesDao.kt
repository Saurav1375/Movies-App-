package com.example.moviesapp.data.local.series

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.moviesapp.data.local.movie.MovieEntity

@Dao
interface SeriesDao {

    @Upsert
    suspend fun upsertAll(movies: List<SeriesEntity>)

    //get all Movie by id
    @Query("SELECT * FROM series_table WHERE id = :id")
    suspend fun getSeriesById(id: Int): SeriesEntity?

    //get list of movies by type
    @Query("SELECT * FROM series_table WHERE type = :type")
    suspend fun getSeriesByType(type: String): List<SeriesEntity>

    //clear db
    @Query("DELETE FROM series_table")
    suspend fun clearAll()


    //clear movies of type
    @Query("DELETE FROM series_table WHERE type = :type")
    suspend fun clearSeriesByType(type: String)



}
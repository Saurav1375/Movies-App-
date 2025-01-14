package com.example.moviesapp.data.local.searchSeries

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SearchSeriesDao {
    @Query("SELECT * FROM search_series WHERE `query` = :searchQuery ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getSeries(searchQuery: String, limit: Int, offset: Int): List<SearchSeriesEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(movies: List<SearchSeriesEntity>)

    @Query("DELETE FROM search_series WHERE `query` = :searchQuery")
    suspend fun clearMovies(searchQuery: String)
}
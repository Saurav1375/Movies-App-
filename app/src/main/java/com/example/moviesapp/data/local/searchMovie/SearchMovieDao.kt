package com.example.moviesapp.data.local.searchMovie

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviesapp.data.local.movie.MovieEntity

@Dao
interface SearchMovieDao {
    @Query("SELECT * FROM search_movies WHERE `query` = :searchQuery ORDER BY id ASC LIMIT :limit OFFSET :offset")
    suspend fun getMovies(searchQuery: String, limit: Int, offset: Int): List<SearchMovieEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<SearchMovieEntity>)

    @Query("DELETE FROM search_movies WHERE `query` = :searchQuery")
    suspend fun clearMovies(searchQuery: String)
}
package com.example.moviesapp.data.local.movie

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Upsert
    suspend fun upsertAll(movies: List<MovieEntity>)

    //get all Movie by id
    @Query("SELECT * FROM movie_table WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    //get list of movies by type
    @Query("SELECT * FROM movie_table WHERE type = :type")
    suspend fun getMoviesByType(type: String): List<MovieEntity>

    @Query("SELECT * FROM movie_table WHERE type = :type")
    fun pagingSource(type: String): PagingSource<Int, MovieEntity>

    //clear db
    @Query("DELETE FROM movie_table")
    suspend fun clearAll()


    //clear movies of type
    @Query("DELETE FROM movie_table WHERE type = :type")
    suspend fun clearMoviesByType(type: String)



}
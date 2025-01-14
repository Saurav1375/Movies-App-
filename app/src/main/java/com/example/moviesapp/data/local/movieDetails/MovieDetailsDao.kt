package com.example.moviesapp.data.local.movieDetails

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert


@Dao
interface MovieDetailsDao {

    //Method to add list of MovieDetailsEntity
    @Upsert
    suspend fun insertMovieDetails(movieDetails: List<MovieDetailsEntity>)

    //method to get movie details by id
    @Query("SELECT * FROM movieDetails_table WHERE id = :id")
    suspend fun getMovieDetailsById(id: Int): MovieDetailsEntity?

    //method to delete all movie details
    @Query("DELETE FROM movieDetails_table")
    suspend fun deleteAllMovieDetails()

    //method to delete movie details by id
    @Query("DELETE FROM movieDetails_table WHERE id = :id")
    suspend fun deleteMovieDetailsById(id: Int)

}
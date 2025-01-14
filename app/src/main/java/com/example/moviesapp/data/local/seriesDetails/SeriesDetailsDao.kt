package com.example.moviesapp.data.local.seriesDetails

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SeriesDetailsDao {

    //method to upsert list of seriesDetails
    @Upsert
    suspend fun insertSeriesDetails(seriesDetails: List<SeriesDetailsEntity>)

    //method to get series details by id
    @Query("SELECT * FROM seriesDetails_table WHERE id = :id")
    suspend fun getSeriesDetailsById(id: Int): SeriesDetailsEntity?

    //method to delete all series details
    @Query("DELETE FROM seriesDetails_table")
    suspend fun deleteAllSeriesDetails()

    //method to delete series details by id
    @Query("DELETE FROM seriesDetails_table WHERE id = :id")
    suspend fun deleteSeriesDetailsById(id: Int)



}
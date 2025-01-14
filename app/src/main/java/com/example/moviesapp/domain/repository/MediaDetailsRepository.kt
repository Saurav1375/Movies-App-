package com.example.moviesapp.domain.repository

import com.example.moviesapp.data.remote.dto.videodto.VideoDto
import com.example.moviesapp.domain.model.Credits
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.model.SeriesDetails
import com.example.moviesapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MediaDetailsRepository {
    fun getMovieDetailsById(
        language: String,
        movieId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<MovieDetails>>

    fun getSeriesDetailsById(
        language: String,
        seriesId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<SeriesDetails>>


    suspend fun getMovieVideoById(
        movieId: Int
    ) : Resource<String>

    suspend fun getSeriesVideoById(
        seriesId: Int
    ) : Resource<String>

    suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int
    ) : Resource<List<Movie>>

    suspend fun getSeriesRecommendations(
        seriesId: Int,
        page: Int
    ) : Resource<List<Series>>

    fun getCreditsDetails(
        type: MediaType,
        mediaId: Int
    ) : Flow<Resource<Credits>>


}
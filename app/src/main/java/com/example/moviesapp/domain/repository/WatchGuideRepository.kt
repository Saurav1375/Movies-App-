package com.example.moviesapp.domain.repository

import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface WatchGuideRepository {

    fun getMoviesByGenre(
        genreId: Int,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean
    ): Flow<Resource<List<Movie>>>
    fun getSeriesByGenre(
        genreId: Int,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean
    ): Flow<Resource<List<Series>>>
    fun getMoviesByQuery(
        query: String,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean,
        year: Int
    ): Flow<Resource<List<Movie>>>
    fun getSeriesByQuery(
        query: String,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean,
        year: Int
    ): Flow<Resource<List<Series>>>
}
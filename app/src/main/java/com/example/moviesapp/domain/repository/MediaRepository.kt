package com.example.moviesapp.domain.repository

import com.example.moviesapp.domain.model.Credits
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MediaRepository {

    fun getMoviesByType(
        language: String,
        type: String,
        page : Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<List<Movie>>>

    fun getSeriesByType(
        language: String,
        type: String,
        page : Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<List<Series>>>



}
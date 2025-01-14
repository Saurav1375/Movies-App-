package com.example.moviesapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.paging.MoviePagingSource
import com.example.moviesapp.data.paging.SeriesPagingSource
import com.example.moviesapp.data.remote.ApiService
import com.example.moviesapp.domain.model.SearchMovie
import com.example.moviesapp.domain.model.SearchSeries
import com.example.moviesapp.domain.repository.SearchMediaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMediaRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val db: MovieDatabase
) : SearchMediaRepository {
    override fun getMovieSearchResultStream(
        query: String,
        language: String,
        isAdult: Boolean,
    ): Flow<PagingData<SearchMovie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MoviePagingSource(api, db, query, language, isAdult)
            }
        ).flow
    }

    override fun getSeriesSearchResultStream(
        query: String,
        language: String,
        isAdult: Boolean,
    ): Flow<PagingData<SearchSeries>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SeriesPagingSource(api, db, query, language, isAdult)
            }
        ).flow
    }
}
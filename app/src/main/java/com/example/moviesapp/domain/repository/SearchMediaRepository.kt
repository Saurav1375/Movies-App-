package com.example.moviesapp.domain.repository

import androidx.paging.PagingData
import com.example.moviesapp.domain.model.SearchMovie
import com.example.moviesapp.domain.model.SearchSeries
import kotlinx.coroutines.flow.Flow

interface SearchMediaRepository {
    fun getMovieSearchResultStream(query: String, language: String, isAdult: Boolean): Flow<PagingData<SearchMovie>>
    fun getSeriesSearchResultStream(query: String, language: String, isAdult: Boolean): Flow<PagingData<SearchSeries>>

}
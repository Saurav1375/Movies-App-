package com.example.moviesapp.presentation.mediadetails_screen

import com.example.moviesapp.domain.model.Credits
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.model.SeriesDetails

data class MediaDetailsState(
    val movieDetails: MovieDetails? = null,
    val seriesDetails: SeriesDetails? = null,
    val movieRecommendations: List<Movie> = emptyList(),
    val seriesRecommendations: List<Series> = emptyList(),
    val credits: Credits? = null,
    val isLoading: Boolean = false,
    val videoUrl: String? = null,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
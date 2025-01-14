package com.example.moviesapp.presentation.home_screen

import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series

data class MovieState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val movieType: MovieType = MovieType(),
    val seriesType: SeriesType = SeriesType(),
    val mediaType: MediaType = MediaType.MOVIE,
    val language: String = "en-US",
    val error: String? = null
)

data class MovieType(
    val theatreMovie: List<Movie> = emptyList(),
    val popularMovies: List<Movie> = emptyList(),
    val upcomingMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
)

data class SeriesType(
    val airingTodaySeries: List<Series> = emptyList(),
    val onTheAirSeries: List<Series> = emptyList(),
    val popularSeries: List<Series> = emptyList(),
    val topRatedSeries: List<Series> = emptyList(),
)
package com.example.moviesapp.presentation.watchguide_screen

import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series

data class WatchGuideState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val movieGenre: List<Movie> = emptyList(),
    val seriesGenre: List<Series> = emptyList(),
    val mediaType: MediaType = MediaType.MOVIE,
    val language: String = "en-US",
    val error: String? = null
)

//data class MovieGenreType(
//    val actionMovies: List<Movie> = emptyList(),
//    val adventureMovies: List<Movie> = emptyList(),
//    val animationMovies: List<Movie> = emptyList(),
//    val comedyMovies: List<Movie> = emptyList(),
//    val crimeMovies: List<Movie> = emptyList(),
//    val documentaryMovies: List<Movie> = emptyList(),
//    val dramaMovies: List<Movie> = emptyList(),
//    val familyMovies: List<Movie> = emptyList(),
//    val fantasyMovies: List<Movie> = emptyList(),
//    val historyMovies: List<Movie> = emptyList(),
//    val horrorMovies: List<Movie> = emptyList(),
//    val musicMovies: List<Movie> = emptyList(),
//    val mysteryMovies: List<Movie> = emptyList(),
//    val romanceMovies: List<Movie> = emptyList(),
//    val sciFiMovies: List<Movie> = emptyList(),
//    val tvMovies: List<Movie> = emptyList(),
//    val thrillerMovies: List<Movie> = emptyList(),
//    val warMovies: List<Movie> = emptyList(),
//    val westernMovies: List<Movie> = emptyList()
//)
//
//data class SeriesGenreType(
//    val actionAndAdventureSeries: List<Series> = emptyList(),
//    val animationSeries: List<Series> = emptyList(),
//    val comedySeries: List<Series> = emptyList(),
//    val crimeSeries: List<Series> = emptyList(),
//    val documentarySeries: List<Series> = emptyList(),
//    val dramaSeries: List<Series> = emptyList(),
//    val familySeries: List<Series> = emptyList(),
//    val kidsSeries: List<Series> = emptyList(),
//    val mysterySeries: List<Series> = emptyList(),
//    val newsSeries: List<Series> = emptyList(),
//    val realitySeries: List<Series> = emptyList(),
//    val sciFiAndFantasySeries: List<Series> = emptyList(),
//    val soapSeries: List<Series> = emptyList(),
//    val talkSeries: List<Series> = emptyList(),
//    val warAndPoliticsSeries: List<Series> = emptyList(),
//    val westernSeries: List<Series> = emptyList(),
//
//)
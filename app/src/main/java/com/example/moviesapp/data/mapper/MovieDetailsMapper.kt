package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.local.movieDetails.MovieDetailsEntity
import com.example.moviesapp.data.remote.dto.moviedetailsdto.MovieDetailsDto
import com.example.moviesapp.domain.model.MovieDetails

fun MovieDetailsDto.toMovieDetailsEntity(): MovieDetailsEntity {
    return MovieDetailsEntity(
        id = id ?: 0,
        title = title ?: "",
        posterPath = posterPath?: "",
        backdropPath = backdropPath ?: "",
        overview = overview ?: "",
        isAdult = adult ?: false,
        releaseDate = releaseDate ?: "",
        runtime = runtime ?: 0,
        status = status ?: "",
        tagline = tagline ?: "",
        popularity = popularity ?: 0.0,
        originalLanguage = originalLanguage ?: "",
        originalTitle = originalTitle ?: "",
        genres = genres?.joinToString { it?.name ?: "" } ?: "",
        voteAverage = voteAverage ?: -1.0,
        voteCount = voteCount ?: -1

    )
}


fun MovieDetailsEntity.toMovieDetails(): MovieDetails {
    return MovieDetails(
        id = id,
        title = title,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        isAdult = isAdult,
        releaseDate = releaseDate,
        runtime = runtime,
        status = status,
        tagline = tagline,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        genres = genres.split(", "),
        voteAverage = voteAverage,
        voteCount = voteCount
    )

}
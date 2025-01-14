package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.local.movie.MovieEntity
import com.example.moviesapp.data.local.searchMovie.SearchMovieEntity
import com.example.moviesapp.data.remote.dto.moviedto.MovieDto
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.SearchMovie

//MovieDto to movie entity mapper
fun MovieDto.toMovieEntity(type: String): List<MovieEntity> {
    return results.map {
        MovieEntity(
            id = it.id,
            title = it.title,
            releaseDate = it.releaseDate,
            posterPath = it.posterPath ?: "",
            type = type
        )
    }.toList()
}

fun MovieEntity.toMovie(): Movie {
    return Movie(
        id = id,
        title = title,
        releaseDate = releaseDate,
        posterPath = posterPath,
        type = type
    )
}

fun MovieDto.toMovie() : List<Movie> {
    return results.map {
        Movie(
            id = it.id,
            title = it.title,
            releaseDate = it.releaseDate,
            posterPath = it.posterPath ?: "",
            type = ""
        )

    }
}

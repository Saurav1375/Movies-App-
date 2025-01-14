package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.local.searchMovie.SearchMovieEntity
import com.example.moviesapp.data.remote.dto.moviedto.MovieDto
import com.example.moviesapp.domain.model.SearchMovie

fun MovieDto.toSearchMovie(): List<SearchMovie> {
    return results.map {
        SearchMovie(
            id = it.id,
            title = it.title,
            posterPath = it.posterPath,
            releaseDate = it.releaseDate
        )
    }
}

fun MovieDto.toSearchMovieEntity(query: String, page: Int): List<SearchMovieEntity> {
    return results.map {
        SearchMovieEntity(
            id = it.id,
            title = it.title,
            posterPath = it.posterPath,
            releaseDate = it.releaseDate,
            query = query,
            page = page
        )
    }
}

fun SearchMovieEntity.toSearchMovie(): SearchMovie {
    return SearchMovie(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate
    )
}
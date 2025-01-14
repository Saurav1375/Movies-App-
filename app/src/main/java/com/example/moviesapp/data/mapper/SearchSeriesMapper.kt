package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.local.searchSeries.SearchSeriesEntity
import com.example.moviesapp.data.remote.dto.seriesdto.SeriesDto
import com.example.moviesapp.domain.model.SearchSeries

fun SeriesDto.toSearchSeries(): List<SearchSeries> {
    return results.map {
        SearchSeries(
            id = it.id,
            title = it.name,
            posterPath = it.posterPath,
            releaseDate = it.firstAirDate ?: ""
        )
    }
}

fun SeriesDto.toSearchSeriesEntity(query: String, page: Int): List<SearchSeriesEntity> {
    return results.map {
        SearchSeriesEntity(
            id = it.id,
            title = it.name,
            posterPath = it.posterPath,
            releaseDate = it.firstAirDate ?: "",
            query = query,
            page = page
        )
    }
}

fun SearchSeriesEntity.toSearchSeries(): SearchSeries {
    return SearchSeries(
        id = id,
        title = title,
        posterPath = posterPath,
        releaseDate = releaseDate
    )
}
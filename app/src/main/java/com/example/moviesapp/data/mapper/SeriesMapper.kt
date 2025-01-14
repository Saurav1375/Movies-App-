package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.local.series.SeriesEntity
import com.example.moviesapp.data.remote.dto.seriesdto.SeriesDto
import com.example.moviesapp.domain.model.SearchSeries
import com.example.moviesapp.domain.model.Series

fun SeriesDto.toSeriesEntity(type: String): List<SeriesEntity> {
    return results.map {
        SeriesEntity(
            id = it.id,
            name = it.name,
            firstAirDate = it.firstAirDate ?: "",
            type = type,
            posterPath = it.posterPath ?: ""
        )
    }
}

fun SeriesEntity.toSeries(): Series {
    return Series(
        id = id,
        name = name,
        firstAirDate = firstAirDate,
        posterPath = posterPath,
        type = type,
    )

}

fun SeriesDto.toSeries(): List<Series> {
    return results.map {
        Series(
            id = it.id,
            name = it.name,
            firstAirDate = it.firstAirDate ?: "",
            posterPath = it.posterPath ?: "",
            type = ""
        )
    }
}


fun Series.toSearchSeries(): SearchSeries {
    return SearchSeries(
        id = id,
        title = name,
        posterPath = posterPath,
        releaseDate = firstAirDate
    )

}


fun SearchSeries.toSeries() : Series {
    return Series (
        id = id,
        name = title,
        firstAirDate = releaseDate,
        posterPath = posterPath ?: "",
        type = ""
    )
}
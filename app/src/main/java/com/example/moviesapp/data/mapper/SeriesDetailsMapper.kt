package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.local.seriesDetails.SeriesDetailsEntity
import com.example.moviesapp.data.remote.dto.seriesdetailsdto.SeriesDetailsDto
import com.example.moviesapp.domain.model.SeriesDetails

fun SeriesDetailsDto.toSeriesDetailsEntity(): SeriesDetailsEntity {
    return SeriesDetailsEntity(
        id = id ?: 0,
        name = name ?: "",
        posterPath = posterPath ?: "",
        backdropPath = backdropPath ?: "",
        overview = overview ?: "",
        isAdult = adult ?: false,
        firstAirDate = firstAirDate ?: "",
        numberOfEpisodes = numberOfEpisodes ?: 0,
        numberOfSeasons = numberOfSeasons ?: 0,
        status = status ?: "",
        tagline = tagline ?: "",
        popularity = popularity ?: 0.0,
        originalLanguage = originalLanguage ?: "",
        originalName = originalName ?: "",
        genres = genres?.joinToString { it?.name ?: "" } ?: "",
        voteAverage = voteAverage ?: -1.0,
        voteCount = voteCount ?: -1

    )
}

fun SeriesDetailsEntity.toSeriesDetails(): SeriesDetails {
    return SeriesDetails(
        id = id,
        title = name,
        posterPath = posterPath,
        backdropPath = backdropPath,
        overview = overview,
        isAdult = isAdult,
        firstAirDate = firstAirDate,
        status = status,
        tagline = tagline,
        popularity = popularity,
        originalLanguage = originalLanguage,
        originalName = originalName,
        genres = genres.split(", "),
        numberOfEpisodes = numberOfEpisodes,
        numberOfSeasons = numberOfSeasons,
        voteAverage = voteAverage,
        voteCount = voteCount
    )


}
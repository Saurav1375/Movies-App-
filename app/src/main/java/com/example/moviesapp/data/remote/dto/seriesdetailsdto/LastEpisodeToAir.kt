package com.example.moviesapp.data.remote.dto.seriesdetailsdto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LastEpisodeToAir(
    @Json(name = "air_date") val airDate: String?,
    @Json(name = "episode_number") val episodeNumber: Int?,
    @Json(name = "episode_type") val episodeType: String?,
    val id: Int?,
    val name: String?,
    val overview: String?,
    @Json(name = "production_code") val productionCode: String?,
    val runtime: Int?,
    @Json(name = "season_number") val seasonNumber: Int?,
    @Json(name = "show_id") val showId: Int?,
    @Json(name = "still_path") val stillPath: String?,
    @Json(name = "vote_average") val voteAverage: Double?,
    @Json(name = "vote_count") val voteCount: Int?
)
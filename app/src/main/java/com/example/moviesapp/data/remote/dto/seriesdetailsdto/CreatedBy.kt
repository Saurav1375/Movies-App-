package com.example.moviesapp.data.remote.dto.seriesdetailsdto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreatedBy(
    @Json(name = "credit_id") val creditId: String?,
    val gender: Int?,
    val id: Int?,
    val name: String?,
    @Json(name = "original_name") val originalName: String?,
    @Json(name = "profile_path") val profilePath: String?
)
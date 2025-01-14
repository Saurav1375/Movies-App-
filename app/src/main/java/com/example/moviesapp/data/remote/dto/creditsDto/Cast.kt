package com.example.moviesapp.data.remote.dto.creditsDto


import com.squareup.moshi.Json

data class Cast(
    val adult: Boolean?,
    val character: String?,
    val gender: Int?,
    val id: Int?,
    @Json(name = "known_for_department") val knownForDepartment: String?,
    val name: String?,
    val order: Int?,
    @Json(name = "original_name") val originalName: String?,
    val popularity: Double?,
    @Json(name = "profile_path") val profilePath: String?
)
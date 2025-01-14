package com.example.moviesapp.data.remote.dto.seriesdetailsdto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Network(
    val id: Int?,
    @Json(name = "logo_path") val logoPath: String?,
    val name: String?,
    @Json(name = "origin_country") val originCountry: String?
)
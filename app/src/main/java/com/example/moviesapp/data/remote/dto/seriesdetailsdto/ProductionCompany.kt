package com.example.moviesapp.data.remote.dto.seriesdetailsdto


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductionCompany(
    val id: Int?,
    @Json(name = "logo_path") val logoPath: Any?,
    val name: String?,
    @Json(name = "origin_country") val originCountry: String?
)
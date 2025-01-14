package com.example.moviesapp.data.remote.dto.videodto


import com.squareup.moshi.Json

data class VideoResult(
    val id: String,
    val key: String,
    val name: String?,
    val type: String?
)
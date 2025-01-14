package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.remote.dto.creditsDto.CreditsDto
import com.example.moviesapp.domain.model.Cast
import com.example.moviesapp.domain.model.Credits


fun CreditsDto.toCredits(): Credits {
    return Credits(
        id = this.id ?: 0,
        cast = this.cast?.map { cast ->
            cast?.let {
                Cast(
                    character = it.character ?: "",
                    name = it.name ?: "",
                    originalName = it.originalName ?: "",
                    profilePath = it.profilePath ?: ""
                )
            } ?: Cast("", "", "", "")
        } ?: emptyList()
    )
}
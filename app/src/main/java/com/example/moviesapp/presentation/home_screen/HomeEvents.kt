package com.example.moviesapp.presentation.home_screen

import com.example.moviesapp.domain.model.MediaType

sealed class HomeEvents {
    data object Refresh : HomeEvents()
    data class OnMediaChange(val mediaType: MediaType) : HomeEvents()
    data class OnLanguageChange(val language: String) : HomeEvents()

}
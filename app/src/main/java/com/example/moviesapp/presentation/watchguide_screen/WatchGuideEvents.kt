package com.example.moviesapp.presentation.watchguide_screen

import com.example.moviesapp.domain.model.MediaType

sealed class WatchGuideEvents {
    data class OnMediaChange(val mediaType: MediaType) : WatchGuideEvents()
    data object Refresh : WatchGuideEvents()
    data class OnSearchQueryChange(val query: String) : WatchGuideEvents()
}
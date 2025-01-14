package com.example.moviesapp.presentation.mediadetails_screen

sealed class MediaDetailsEvent {
    data object OnAddClick : MediaDetailsEvent()
    data object OnRefresh : MediaDetailsEvent()

}
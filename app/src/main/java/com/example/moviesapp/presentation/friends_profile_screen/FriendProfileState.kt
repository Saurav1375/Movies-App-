package com.example.moviesapp.presentation.friends_profile_screen

import com.example.moviesapp.domain.model.UserData

data class FriendProfileState(
    val userData: UserData = UserData(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null
)
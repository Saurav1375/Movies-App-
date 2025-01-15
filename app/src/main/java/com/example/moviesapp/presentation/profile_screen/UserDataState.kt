package com.example.moviesapp.presentation.profile_screen

import com.example.moviesapp.domain.model.UserData

data class UserDataState(
    val userData: UserData = UserData(),
    val isLoading: Boolean = false,
    val query: String = "",
    val queryUsers: List<UserData> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)
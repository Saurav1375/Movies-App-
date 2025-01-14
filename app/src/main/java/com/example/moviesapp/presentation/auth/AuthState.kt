package com.example.moviesapp.presentation.auth

import com.example.moviesapp.domain.model.User

sealed class AuthState {
    data object Initial : AuthState()
    data object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    data object SignedOut : AuthState()
}
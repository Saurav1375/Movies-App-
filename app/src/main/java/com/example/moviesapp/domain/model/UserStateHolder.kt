package com.example.moviesapp.domain.model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

object UserStateHolder  {
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()

    fun setUser(user: User?) {
        _user.value = user
    }

    fun clearUser() {
        _user.value = null
    }
}
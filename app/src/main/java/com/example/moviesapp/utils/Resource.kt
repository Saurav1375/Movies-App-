package com.example.moviesapp.utils

sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T, message: String? = null) : Resource<T>(data, message)
    class Loading<T>(val isLoading : Boolean = true) : Resource<T>()
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
}
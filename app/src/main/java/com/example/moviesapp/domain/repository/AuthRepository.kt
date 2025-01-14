package com.example.moviesapp.domain.repository

import com.example.moviesapp.domain.model.User
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.utils.Resource

interface AuthRepository {
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signOut()
    suspend fun addUserData(userData: UserData, userId: String) : Resource<Unit>
    fun getCurrentUser(): User?
}
package com.example.moviesapp.data.repository

import android.util.Log
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.User
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.domain.repository.AuthRepository
import com.example.moviesapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AuthRepository {
    override suspend fun signInWithGoogle(idToken: String): Result<User> = try {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        val firebaseUser = result.user

        if (firebaseUser != null) {
            val userData = UserData(
                userId = firebaseUser.uid,
                username = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: "",
                profilePictureUrl = firebaseUser.photoUrl.toString(),
            )
            addUserData(userData, firebaseUser.uid)
            Result.success(firebaseUser.toUser())

        } else {
            Result.failure(Exception("Sign in failed"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun addUserData(userData: UserData, userId: String): Resource<Unit> {
        try {
            val reference = database.getReference("users/$userId")
            reference.setValue(userData).await()
            val reference1 = database.getReference("users/$userId/medialist/${userId+"FAVORITE"}")
            val childUpdates = mapOf<String, Any>(
                "name" to "Favorites",
                "type" to ListType.FAVOURITES.name,
                "id" to userId+"FAVORITE"
            )
            reference1.updateChildren(childUpdates).await()

            val reference2 = database.getReference("users/$userId/medialist/${userId+"WATCHED"}")
            val childUpdates2 = mapOf<String, Any>(
                "name" to "Watched",
                "type" to ListType.WATCHED.name,
                "id" to userId+"WATCHED"
            )
            reference2.updateChildren(childUpdates2).await()
            Log.d("TAG", "addUserData: $userData")
            return Resource.Success(Unit)

        } catch (e : Exception) {
            Log.d("TAG", "addUserData: ${e.message}")
            return Resource.Error(e.message ?: "Failed to add user data")
        }
    }

    override fun getCurrentUser(): User? {
        return auth.currentUser?.toUser()
    }

    private fun FirebaseUser.toUser(): User {
        return User(
            id = uid,
            name = displayName,
            email = email,
            photoUrl = photoUrl?.toString(),
        )
    }
}
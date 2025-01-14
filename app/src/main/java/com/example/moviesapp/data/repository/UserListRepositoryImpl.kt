package com.example.moviesapp.data.repository

import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.mapper.toEntity
import com.example.moviesapp.data.mapper.toMediaList
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.domain.repository.UserListRepository
import com.example.moviesapp.utils.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class UserListRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val db: MovieDatabase
) : UserListRepository {

    private val dao = db.mediaListDao

    override suspend fun addUserData(userData: UserData, userId: String): Resource<Unit> {
        try {
            val reference = database.getReference("users/$userId")
            reference.setValue(userData).await()
            return Resource.Success(Unit)

        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Failed to add user data")
        }
    }

    //this will just add the roue of the Favorites and Watched list
    override suspend fun addInitialLists(userId: String): Resource<Unit> {
        try {
            val reference = database.getReference("users/$userId/medialist/${UUID.randomUUID()}")
            val childUpdates = mapOf<String, Any>(
                "name" to "Favorites",
                "type" to ListType.FAVOURITES.name
            )
            reference.updateChildren(childUpdates).await()

            val reference2 = database.getReference("users/$userId/medialist/${UUID.randomUUID()}")
            val childUpdates2 = mapOf<String, Any>(
                "name" to "Watched",
                "type" to ListType.WATCHED.name
            )
            reference2.updateChildren(childUpdates2).await()
            return Resource.Success(Unit)
        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Failed to add initial lists")

        }

    }

    override suspend fun addMediaToListByListId(
        listId: String,
        media: Media,
        userId: String
    ): Resource<Unit> {
        return try {
            dao.addMediaToList(listId, media)
            dao.getMediaListById(listId).collect { list ->
                if (list != null) {
                    val mediaList = list.toMediaList()
                    val reference = database.getReference("users/$userId/medialist")
                    val childUpdates = mapOf<String, Any>(
                        list.id to mediaList
                    )
                    reference.updateChildren(childUpdates).await()

                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add media to list")
        }
    }

    override suspend fun removeMediaFromListByListId(
        listId: String,
        media: Media,
        userId: String
    ): Resource<Unit> {
        return try {
            dao.removeMediaFromList(listId, media.id)
            val reference = database.getReference("users/$userId/medialist/$listId/list")
            val childUpdates = mapOf<String, Any?>(
                media.id.toString() to null
            )
            reference.updateChildren(childUpdates).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove media from list")
        }
    }

    override fun getListById(listId: String, userId: String): Flow<Resource<MediaList>> = flow {
        try {
            dao.getMediaListById(listId).collect { list ->
                if (list != null) {
                    val mediaList = list.toMediaList()
                    emit(Resource.Success(mediaList))

                } else {
                    val reference = database.getReference("users/$userId/medialist/$listId")
                    val snapshot = reference.get().await()
                    val mediaList = snapshot.getValue(MediaList::class.java)
                    if (mediaList != null) {
                        dao.upsertMediaList(listOf(mediaList.toEntity()))
                        val updatedLocalList =
                            dao.getMediaListById(listId).firstOrNull()?.toMediaList()
                        emit(Resource.Success(updatedLocalList ?: MediaList()))
                    } else {
                        emit(Resource.Error("MediaList not found"))
                    }


                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }

    }

    override fun getAllLists(userId: String): Flow<Resource<List<MediaList>>> = flow {
        try {
            dao.getAllMediaLists().collect { localList ->
                if (localList.isNotEmpty()) {
                    val mediaList = localList.map { it.toMediaList() }
                    println("Local data found: $mediaList")
                    emit(Resource.Success(mediaList))
                } else {
                    // Fetch data from Firebase if local data is empty
                    val reference = database.getReference("users/$userId/medialist")
                    val snapshot = reference.get().await()

                    val watchLists =
                        snapshot.children.mapNotNull { it.getValue(MediaList::class.java) }
                    if (watchLists.isNotEmpty()) {
                        // Store fetched data in Room
                        dao.upsertMediaList(watchLists.map { it.toEntity() })
                        val updatedLocalList = dao.getAllMediaLists()
                            .firstOrNull() // Get the updated local list
                            ?.map { it.toMediaList() }
                        emit(Resource.Success(updatedLocalList ?: emptyList()))
                    } else {
                        emit(Resource.Error("No data found in Firebase"))
                    }
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "An unknown error occurred"))
        }
    }


    override suspend fun addList(list: MediaList, userId: String): Resource<Unit> {
        return try {
            dao.upsertMediaList(listOf(list.toEntity()))
            dao.getMediaListById(list.id).collect { entity ->
                if (entity != null) {
                    val insertedList = entity.toMediaList()
                    val reference = database.getReference("users/$userId/medialist")
                    val childUpdates = mapOf<String, Any>(
                        insertedList.id.toString() to insertedList
                    )
                    reference.updateChildren(childUpdates).await()
                }

            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add list")
        }
    }

    override suspend fun removeList(list: MediaList, userId: String): Resource<Unit> {
        return try {
            dao.deleteMediaListById(list.id)
            val reference = database.getReference("users/$userId/medialist")
            val childUpdates = mapOf<String, Any?>(
                list.id.toString() to null
            )
            reference.updateChildren(childUpdates).await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove list")
        }
    }

    //get user data from firebase database
    override suspend fun getUserData(userId: String): Flow<Resource<UserData>> = callbackFlow {
        val reference = database.getReference("users/$userId")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserData::class.java)
                if (userData != null) {
                    trySendBlocking(Resource.Success(userData))
                } else {
                    trySendBlocking(Resource.Error("User data not found"))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Resource.Error(error.message))

            }

        }
        reference.addValueEventListener(listener)
        awaitClose { reference.removeEventListener(listener) }
    }

    override suspend fun updateList(list: MediaList, userId: String): Resource<Unit> {
        TODO("Not yet implemented")
    }
}

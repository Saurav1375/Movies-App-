package com.example.moviesapp.data.repository

import android.database.sqlite.SQLiteException
import android.util.Log
import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.mapper.toEntity
import com.example.moviesapp.data.mapper.toMediaList
import com.example.moviesapp.domain.model.ListType
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.domain.repository.UserListRepository
import com.example.moviesapp.utils.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
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

    //this will just add the roue of the Favorites and Watched list
    override suspend fun addWatchList(userId: String, name: String): Resource<Unit> {
        try {
            val id = UUID.randomUUID()

            dao.upsertMediaList(
                listOf(
                    MediaList(
                        id = id.toString(),
                        name = name,
                        type = ListType.WATCHLIST.name,
                        list = listOf()
                    ).toEntity()
                )
            )
            val reference1 = database.getReference("users/$userId/medialist/${id}")
            val childUpdates1 = mapOf<String, Any>(
                "name" to name,
                "type" to ListType.WATCHLIST.name,
                "id" to id.toString()
            )
            reference1.updateChildren(childUpdates1).await()
            return Resource.Success(Unit)

        } catch (e: Exception) {
            Log.d("TAG", "addUserData: ${e.message}")
            return Resource.Error(e.message ?: "Failed to add user data")
        }

    }

    override suspend fun addMediaToListByListId(
        listId: String,
        media: Media,
        userId: String
    ): Resource<Unit> {
        return try {
            dao.getMediaListById(listId).collect { list ->
                if (list != null) {
                    val mediaList = list.toMediaList()

                    // Check for duplicate based on unique identifier (e.g., media.id)
                    if (mediaList.list.none { it.id == media.id }) {
                        // Add media to the local database
                        dao.addMediaToList(listId, media)

                        // Update Firebase database
                        val updatedMediaList = mediaList.copy(list = mediaList.list + media)
                        val reference = database.getReference("users/$userId/medialist")
                        val childUpdates = mapOf<String, Any>(
                            list.id to updatedMediaList
                        )
                        reference.updateChildren(childUpdates).await()

                        println("Media added to list: $listId")
                    } else {
                        println("Media already exists in the list: $listId")
                    }
                }
            }
            Resource.Success(Unit)
        } catch (e: Exception) {
            println(e.message)
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

                    if (!snapshot.exists()) return@collect

                    val name = snapshot.child("name").getValue(String::class.java)
                    val type = snapshot.child("type").getValue(String::class.java)
                    val userListId = snapshot.child("id").getValue(String::class.java)
                    val listSnapshot = snapshot.child("list")
                    val items = mutableListOf<Media>()

                    for (listSnap in listSnapshot.children) {
                        val mediaId = listSnap.child("id").getValue(Int::class.java)
                        val title = listSnap.child("title").getValue(String::class.java)
                        val posterPath = listSnap.child("posterPath").getValue(String::class.java)
                        val mediaType = listSnap.child("type").getValue(String::class.java)

                        items.add(
                            Media(
                                id = mediaId ?: 0,
                                title = title ?: "",
                                posterPath = posterPath ?: "",
                                type = mediaType ?: ""
                            )
                        )


                    }
                    val mediaList = MediaList(
                        id = userListId ?: "",
                        name = name ?: "",
                        type = type ?: "",
                        list = items
                    )

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

    override fun getAllLists(userId: String, forceRemoteFetch: Boolean): Flow<Resource<List<MediaList>>> = flow {
        emit(Resource.Loading())

        try {
            if (forceRemoteFetch) {
                val lists = fetchListsFromFirebase(userId)
                Log.d("Repository", "Force fetching lists from Firebase: $lists")

                if (lists.isNotEmpty()) {
                    emit(Resource.Success(lists))
                } else {
                    emit(Resource.Error("No media lists found"))
                }
            } else {
                var listFromFirebase = try {
                    fetchListsFromFirebase(userId)

                } catch (e : Exception) {
                    Log.d("Repository", "Error fetching lists from Firebase: $e")
                    emptyList()
                }
                dao.getAllMediaLists().collect { localList ->
                    if(listFromFirebase.isEmpty()){
                        listFromFirebase = localList.map { it.toMediaList() }
                    }
                    if (localList.isNotEmpty() && (listFromFirebase == localList.map { it.toMediaList() })) {
                        Log.d("Repository", "Fetched lists from Local: $localList")
                        emit(Resource.Success(localList.map { it.toMediaList() }))
                    } else {
                        val lists = fetchListsFromFirebase(userId)
                        Log.d("Repository", "Fetched lists from Firebase: $lists")

                        if (lists.isNotEmpty()) {
                            dao.upsertMediaList(lists.map { it.toEntity() })
                            emit(Resource.Success(lists))
                        } else {
                            emit(Resource.Error("No media lists found"))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(
                Resource.Error(
                    message = when (e) {
                        is FirebaseException -> "Failed to connect to server: ${e.message}"
                        is SQLiteException -> "Database error: ${e.message}"
                        else -> "An unexpected error occurred: ${e.message}"
                    }
                )
            )
        }
    }

    private suspend fun fetchListsFromFirebase(userId: String): List<MediaList> {
        val reference = database.getReference("users/$userId/medialist")
        val snapshot = reference.get().await()

        return snapshot.children.mapNotNull { mediaListSnapshot ->
            try {
                val name = mediaListSnapshot.child("name").getValue(String::class.java)
                val type = mediaListSnapshot.child("type").getValue(String::class.java)
                val userListId = mediaListSnapshot.child("id").getValue(String::class.java)

                if (name == null || type == null || userListId == null) {
                    Log.w("Repository", "Invalid media list data: missing required fields")
                    return@mapNotNull null
                }

                val items = mediaListSnapshot.child("list").children.mapNotNull { listSnapshot ->
                    try {
                        Media(
                            id = listSnapshot.child("id").getValue(Int::class.java)
                                ?: return@mapNotNull null,
                            title = listSnapshot.child("title").getValue(String::class.java)
                                ?: return@mapNotNull null,
                            posterPath = listSnapshot.child("posterPath")
                                .getValue(String::class.java) ?: "",
                            type = listSnapshot.child("type").getValue(String::class.java)
                                ?: return@mapNotNull null
                        )
                    } catch (e: Exception) {
                        Log.e("Repository", "Error parsing media item", e)
                        null
                    }
                }

                MediaList(
                    id = userListId,
                    name = name,
                    type = type,
                    list = items
                )
            } catch (e: Exception) {
                Log.e("Repository", "Error parsing media list", e)
                null
            }
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

    override suspend fun searchFriend(
        query: String,
        currentUserId: String
    ): Flow<Resource<List<UserData>>> =
        callbackFlow {
            trySend(Resource.Loading())

            val reference = database.getReference("users")
            val lowercaseQuery = query.lowercase()

            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val users = snapshot.children.mapNotNull { child ->
                        child.getValue(UserData::class.java)?.copy(userId = child.key ?: "")
                    }.filter { userData ->
                        userData.userId != currentUserId &&
                                (userData.appHandle.lowercase().contains(lowercaseQuery) ||
                                        userData.email.lowercase().contains(lowercaseQuery) ||
                                        userData.username.lowercase().contains(lowercaseQuery))
                    }
                    trySend(Resource.Success(users))
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Resource.Error(error.message))
                }
            }
            reference.addValueEventListener(valueEventListener)

            awaitClose {
                reference.removeEventListener(valueEventListener)
            }
        }

    override suspend fun addFriend(userId: String, friendId: String): Resource<Unit> {
        try {
            val reference1 = database.getReference("users/$userId")
            val reference2 = database.getReference("users/$friendId")
            val snapshot1 = reference1.get().await()
            val snapshot2 = reference2.get().await()
            val userFriendsList = snapshot1.child("friends")
                .getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            val friendFriendsList = snapshot2.child("friends")
                .getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            if (userFriendsList.contains(friendId) && friendFriendsList.contains(userId)) {
                reference1.child("friends").setValue(userFriendsList.filter { it != friendId })
                reference2.child("friends").setValue(friendFriendsList.filter { it != userId })
            } else {
                reference1.child("friends").setValue(userFriendsList + listOf(friendId))
                reference2.child("friends").setValue(friendFriendsList + listOf(userId))
            }
            return Resource.Success(Unit)

        } catch (e: Exception) {
            return Resource.Error(e.message ?: "Failed to add friend")
        }
    }

    override suspend fun getFriendsData(userId: String): Flow<Resource<List<UserData>>> =
        callbackFlow {
            trySend(Resource.Loading())

            val friendsRef = database.getReference("users/$userId/friends")
            val friendListeners = mutableMapOf<String, ValueEventListener>()
            val friendsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val userFriendsList =
                            snapshot.getValue(object : GenericTypeIndicator<List<String>>() {})
                                ?: emptyList()

                        val friendsData = mutableListOf<UserData>()

                        if (userFriendsList.isEmpty()) {
                            trySend(Resource.Success(friendsData))
                            return
                        }

                        var loadedFriends = 0

                        // Remove old listeners before adding new ones
                        friendListeners.forEach { (friendId, listener) ->
                            database.getReference("users/$friendId").removeEventListener(listener)
                        }
                        friendListeners.clear()

                        for (friendId in userFriendsList) {
                            val friendRef = database.getReference("users/$friendId")
                            val friendListener = object : ValueEventListener {
                                override fun onDataChange(friendSnapshot: DataSnapshot) {
                                    val friendData = friendSnapshot.getValue(UserData::class.java)
                                    if (friendData != null) {
                                        val existingIndex =
                                            friendsData.indexOfFirst { it.userId == friendData.userId }
                                        if (existingIndex != -1) {
                                            friendsData[existingIndex] = friendData
                                        } else {
                                            friendsData.add(friendData)
                                        }
                                    }

                                    loadedFriends++
                                    if (loadedFriends == userFriendsList.size) {
                                        trySend(Resource.Success(friendsData.toList()))
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    trySend(Resource.Error(error.message))
                                }
                            }

                            friendListeners[friendId] = friendListener
                            friendRef.addValueEventListener(friendListener)
                        }

                    } catch (e: Exception) {
                        trySend(Resource.Error(e.message ?: "Unknown error occurred"))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    trySend(Resource.Error(error.message))
                }
            }

            friendsRef.addValueEventListener(friendsListener)

            awaitClose {
                friendListeners.forEach { (friendId, listener) ->
                    database.getReference("users/$friendId").removeEventListener(listener)
                }
                friendsRef.removeEventListener(friendsListener)
            }
        }

}

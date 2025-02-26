package com.example.moviesapp.domain.repository

import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaList
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserListRepository {
    suspend fun addMediaToListByListId(listId : String, media : Media, userId : String) : Resource<Unit>
    suspend fun removeMediaFromListByListId(listId : String, media : Media, userId: String) : Resource<Unit>
    fun getListById(listId : String, userId: String) : Flow<Resource<MediaList>>
    fun getAllLists(userId: String, forceRemoteFetch : Boolean) : Flow<Resource<List<MediaList>>>
    suspend fun addList(list: MediaList, userId: String) : Resource<Unit>
    suspend fun removeList(list: MediaList, userId: String) : Resource<Unit>
    suspend fun getUserData(userId: String) : Flow<Resource<UserData>>
    suspend fun addWatchList(userId: String, name : String) : Resource<Unit>
    suspend fun searchFriend(query: String, currentUserId : String) : Flow<Resource<List<UserData>>>
    suspend fun addFriend(userId: String, friendId : String) : Resource<Unit>
    suspend fun getFriendsData(userId: String) : Flow<Resource<List<UserData>>>
}
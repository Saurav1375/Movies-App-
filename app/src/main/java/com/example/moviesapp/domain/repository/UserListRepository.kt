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
    fun getAllLists(userId: String) : Flow<Resource<List<MediaList>>>
    suspend fun addList(list: MediaList, userId: String) : Resource<Unit>
    suspend fun removeList(list: MediaList, userId: String) : Resource<Unit>
    suspend fun updateList(list: MediaList, userId: String) : Resource<Unit>
    suspend fun addUserData(userData: UserData, userId: String) : Resource<Unit>
    suspend fun getUserData(userId: String) : Flow<Resource<UserData>>
    suspend fun addInitialLists(userId: String) : Resource<Unit>
}
package com.example.moviesapp.data.local.userMediaList

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.moviesapp.data.mapper.toEntity
import com.example.moviesapp.data.mapper.toMediaList
import com.example.moviesapp.domain.model.Media
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.firstOrNull

@Dao
interface MediaListDao {

    @Upsert
    suspend fun upsertMediaList(mediaList: List<MediaListEntity>)

    @Query("SELECT * FROM media_list WHERE id = :id")
    fun getMediaListById(id: String): Flow<MediaListEntity?>

    @Query("DELETE FROM media_list WHERE id = :id")
    suspend fun deleteMediaListById(id: String)

    @Query("SELECT * FROM media_list")
    fun getAllMediaLists(): Flow<List<MediaListEntity>>

    @Query("DELETE FROM media_list")
    suspend fun deleteAllMediaLists()

    @Transaction
    suspend fun addMediaToList(listId: String, media: Media) {
        getMediaListById(listId).let{ entity ->
            val domain = entity.firstOrNull()?.toMediaList()
            val updatedDomain = domain?.copy(list = domain.list.plus(media))
            if (updatedDomain != null) {
                upsertMediaList(listOf(updatedDomain.toEntity()))
            }
        }
    }

    @Transaction
    suspend fun removeMediaFromList(listId: String, mediaId: Int) {
        getMediaListById(listId).let { entity ->
            val domain = entity.firstOrNull()?.toMediaList()
            val updatedDomain = domain?.copy(list = domain.list.filterNot { it.id == mediaId })
            if (updatedDomain != null) {
                upsertMediaList(listOf(updatedDomain.toEntity()))
            }
        }
    }

    //get by type
    @Query("SELECT * FROM media_list WHERE type = :type")
    fun getMediaListsByType(type: String): Flow<List<MediaListEntity>>

    //get by name
    @Query("SELECT * FROM media_list WHERE name = :name")
    fun getMediaListsByName(name: String): Flow<List<MediaListEntity>>

    //get by id and type
    @Query("SELECT * FROM media_list WHERE id = :id AND type = :type")
    fun getMediaListByIdAndType(id: Int, type: String): Flow<MediaListEntity?>

}
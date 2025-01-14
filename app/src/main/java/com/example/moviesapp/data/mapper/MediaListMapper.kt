package com.example.moviesapp.data.mapper

import com.example.moviesapp.data.local.userMediaList.MediaListEntity
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaList
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory


val moshi: Moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()
private val mediaListType = Types.newParameterizedType(List::class.java, Media::class.java)
private val mediaListAdapter = moshi.adapter<List<Media>>(mediaListType)

// Convert MediaList to MediaListEntity
fun MediaList.toEntity(): MediaListEntity {
    val json = mediaListAdapter.toJson(list) // Serialize list to JSON
    return MediaListEntity(
        id = id,
        name = name,
        type = type,
        listJson = json
    )
}

// Convert MediaListEntity back to MediaList
fun MediaListEntity.toMediaList(): MediaList {
    val mediaList = mediaListAdapter.fromJson(listJson) ?: emptyList() // Deserialize JSON to List<Media>
    return MediaList(
        id = id,
        name = name,
        type = type,
        list = mediaList
    )
}
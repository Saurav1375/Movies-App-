package com.example.moviesapp.data.local.userMediaList

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID


@Entity(tableName = "media_list")
data class MediaListEntity(
    val name: String,
    val type: String,
    @PrimaryKey(autoGenerate = false) val id: String,
    val listJson: String
)
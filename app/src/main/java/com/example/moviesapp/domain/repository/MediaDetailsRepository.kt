package com.example.moviesapp.domain.repository

import com.example.moviesapp.domain.model.Credits
import com.example.moviesapp.domain.model.MediaRoom
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Message
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.model.SeriesDetails
import com.example.moviesapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MediaDetailsRepository {
    fun getMovieDetailsById(
        language: String,
        movieId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<MovieDetails>>

    fun getSeriesDetailsById(
        language: String,
        seriesId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<SeriesDetails>>


    suspend fun getMovieVideoById(
        movieId: Int
    ): Resource<String>

    suspend fun getSeriesVideoById(
        seriesId: Int
    ): Resource<String>

    suspend fun getMovieRecommendations(
        movieId: Int,
        page: Int
    ): Resource<List<Movie>>

    suspend fun getSeriesRecommendations(
        seriesId: Int,
        page: Int
    ): Resource<List<Series>>

    fun getCreditsDetails(
        type: MediaType,
        mediaId: Int
    ): Flow<Resource<Credits>>

    suspend fun createRoom(mediaId: Int, mediaType: String): Result<MediaRoom>
    suspend fun getRoom(mediaId: Int, mediaType: String): Result<MediaRoom?>
    suspend fun addMessage(roomId: String, message: Message): Result<Boolean>
    suspend fun editMessage(roomId: String, messageId: String, newText: String): Result<Boolean>
    suspend fun deleteMessage(roomId: String, messageId: String): Result<Boolean>
    suspend fun listenToRoomMessages(roomId: String): Flow<Resource<List<Message>>>
    suspend fun addReaction(
        roomId: String,
        messageId: String,
        emoji: String
    ): Result<Boolean>

    suspend fun toggleLikeMessage(userId: String, roomId: String, messageId: String) : Result<Boolean>
}
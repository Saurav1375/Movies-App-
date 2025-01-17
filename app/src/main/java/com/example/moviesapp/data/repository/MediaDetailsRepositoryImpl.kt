package com.example.moviesapp.data.repository

import android.util.Log
import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.mapper.toCredits
import com.example.moviesapp.data.mapper.toMovie
import com.example.moviesapp.data.mapper.toMovieDetails
import com.example.moviesapp.data.mapper.toMovieDetailsEntity
import com.example.moviesapp.data.mapper.toSeries
import com.example.moviesapp.data.mapper.toSeriesDetails
import com.example.moviesapp.data.mapper.toSeriesDetailsEntity
import com.example.moviesapp.data.remote.ApiService
import com.example.moviesapp.domain.model.Credits
import com.example.moviesapp.domain.model.MediaRoom
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Message
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.model.SeriesDetails
import com.example.moviesapp.domain.repository.MediaDetailsRepository
import com.example.moviesapp.utils.Resource
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.io.IOException
import javax.inject.Inject

class MediaDetailsRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val db: MovieDatabase,
    private val database: FirebaseDatabase
) : MediaDetailsRepository {

    private val movieDetailsDao = db.movieDetailsDao
    private val seriesDetailsDao = db.seriesDetailsDao

    companion object {
        private const val ROOMS_REF = "media_rooms"
        private const val MESSAGES_REF = "messages"
    }

    override fun getMovieDetailsById(
        language: String,
        movieId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<MovieDetails>> = flow {
        emit(Resource.Loading())

        val localMovieDetails = movieDetailsDao.getMovieDetailsById(movieId)
        localMovieDetails?.let { emit(Resource.Success(it.toMovieDetails())) }

        val shouldLoadFromCache = localMovieDetails != null && !fetchFormRemote
        if (shouldLoadFromCache) {
            emit(Resource.Loading(isLoading = false))
            return@flow
        }
        try {
            val response = api.getMovieDetails(movieId, language)
            if (response.isSuccessful) {
                response.body()?.let { movieDetailsDto ->
                    val movieDetailsEntity = movieDetailsDto.toMovieDetailsEntity()
                    movieDetailsDao.deleteMovieDetailsById(movieDetailsEntity.id)
                    movieDetailsDao.insertMovieDetails(listOf(movieDetailsEntity))
                    emit(
                        Resource.Success(
                            movieDetailsDao.getMovieDetailsById(movieDetailsEntity.id)
                                ?.toMovieDetails()!!
                        )
                    )
                }
            }
        } catch (e: IOException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            emit(Resource.Error(message = "Couldn't load data"))

        } catch (e: HttpException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")

            emit(Resource.Error(message = "Couldn't load data"))

        } catch (e: Exception) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            emit(Resource.Error(message = "Couldn't load data"))
        }


    }

    override fun getSeriesDetailsById(
        language: String,
        seriesId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<SeriesDetails>> = flow {
        emit(Resource.Loading())
        val localSeriesDetails = seriesDetailsDao.getSeriesDetailsById(seriesId)
        if (localSeriesDetails != null) {
            emit(Resource.Success(localSeriesDetails.toSeriesDetails()))
        }
        val shouldLoadFromCache = localSeriesDetails != null && !fetchFormRemote
        if (shouldLoadFromCache) {
            emit(Resource.Loading(isLoading = false))
            return@flow
        }
        try {
            val response = api.getSeriesDetails(seriesId, language)
            if (response.isSuccessful) {
                response.body()?.let { seriesDetailsDto ->
                    val seriesDetailsEntity = seriesDetailsDto.toSeriesDetailsEntity()
                    seriesDetailsDao.deleteSeriesDetailsById(seriesDetailsEntity.id)
                    seriesDetailsDao.insertSeriesDetails(listOf(seriesDetailsEntity))
                    emit(Resource.Success(seriesDetailsEntity.toSeriesDetails()))
                }
            }
        } catch (e: IOException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            emit(Resource.Error(message = "Couldn't load data"))

        } catch (e: HttpException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")

            emit(Resource.Error(message = "Couldn't load data"))

        } catch (e: Exception) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            emit(Resource.Error(message = "Couldn't load data"))
        }
    }

    override suspend fun getMovieVideoById(movieId: Int): Resource<String> {
        return try {
            val response = api.getMovieVideos(movieId)
            if (response.isSuccessful) {
                val videoDto = response.body()
                if (videoDto != null && videoDto.results.isNotEmpty()) {
                    val videoResult =
                        videoDto.results.firstOrNull { it.type?.lowercase() == "trailer" }
                    Log.d("MovieRepositoryImpl", "Video Result: ${videoResult?.key}")
                    val videoUrl = videoResult?.key ?: videoDto.results[0].key
                    Resource.Success(videoUrl)
                } else {
                    Log.d("MovieRepositoryImpl", "No video found for the movie")
                    Resource.Error("No video found for the movie")
                }
            } else {
                Log.d("MovieRepositoryImpl", "Failed to fetch video data")
                Resource.Error("Failed to fetch video data")
            }

        } catch (e: IOException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            Resource.Error("Couldn't load data")

        } catch (e: HttpException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            Resource.Error("Couldn't load data")
        } catch (e: Exception) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            Resource.Error("Couldn't load data")
        }
    }

    override suspend fun getSeriesVideoById(seriesId: Int): Resource<String> {
        return try {
            val response = api.getSeriesVideos(seriesId)
            if (response.isSuccessful) {
                val videoDto = response.body()
                if (videoDto != null && videoDto.results.isNotEmpty()) {
                    val videoResult =
                        videoDto.results.firstOrNull { it.type?.lowercase() == "trailer" }
                    Log.d("MovieRepositoryImpl", "Video Result: ${videoResult?.key}")
                    val videoUrl = videoResult?.key ?: videoDto.results[0].key
                    Resource.Success(videoUrl)
                } else {
                    Log.d("MovieRepositoryImpl", "No video found for the movie")
                    Resource.Error("No video found for the movie")
                }
            } else {
                Log.d("MovieRepositoryImpl", "Failed to fetch video data")
                Resource.Error("Failed to fetch video data")
            }

        } catch (e: IOException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            Resource.Error("Couldn't load data")

        } catch (e: HttpException) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            Resource.Error("Couldn't load data")
        } catch (e: Exception) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
            Resource.Error("Couldn't load data")
        }
    }

    override suspend fun getMovieRecommendations(movieId: Int, page: Int): Resource<List<Movie>> {
        return try {
            val response = api.getMovieRecommendations(
                movieId = movieId,
                page = page
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body.toMovie()) // Assuming `toMovie()` returns a `List<Movie>`
                } else {
                    Resource.Error("Empty response body")
                }
            } else {
                Resource.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Log.e("MovieRepositoryImpl", "IOException: ${e.message}", e)
            Resource.Error("Couldn't load data. Please check your internet connection.")
        } catch (e: HttpException) {
            Log.e("MovieRepositoryImpl", "HttpException: ${e.message}", e)
            Resource.Error("Couldn't load data. Server error occurred.")
        } catch (e: Exception) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}", e)
            Resource.Error("An unexpected error occurred.")
        }
    }

    override suspend fun getSeriesRecommendations(
        seriesId: Int,
        page: Int
    ): Resource<List<Series>> {
        return try {
            val response = api.getSeriesRecommendations(
                seriesId = seriesId,
                page = page
            )
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Resource.Success(body.toSeries())
                } else {
                    Resource.Error("Empty response body")
                }
            } else {
                Resource.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: IOException) {
            Log.e("MovieRepositoryImpl", "IOException: ${e.message}", e)
            Resource.Error("Couldn't load data. Please check your internet connection.")
        } catch (e: HttpException) {
            Log.e("MovieRepositoryImpl", "HttpException: ${e.message}", e)
            Resource.Error("Couldn't load data. Server error occurred.")
        } catch (e: Exception) {
            Log.e("MovieRepositoryImpl", "Exception: ${e.message}", e)
            Resource.Error("An unexpected error occurred.")
        }
    }

    override fun getCreditsDetails(type: MediaType, mediaId: Int): Flow<Resource<Credits>> = flow {
        emit(Resource.Loading(isLoading = true))
        try {
            val response = when (type) {
                MediaType.MOVIE -> api.getMovieCredits(mediaId)
                MediaType.SERIES -> api.getSeriesCredits(mediaId)
            }
            if (response.isSuccessful) {
                val creditsDto = response.body()
                if (creditsDto != null) {
                    emit(Resource.Success(creditsDto.toCredits()))
                } else {
                    emit(Resource.Error(message = "Empty response body"))

                }
            }

        } catch (e: IOException) {
            emit(Resource.Error(message = "Couldn't load data"))
        } catch (e: HttpException) {
            emit(Resource.Error(message = "Couldn't load data"))
        } catch (e: Exception) {
            emit(Resource.Error(message = "Couldn't load data"))
        }

    }

    override suspend fun createRoom(mediaId: Int, mediaType: String): Result<MediaRoom> = try {
        val roomId = "${mediaType}_$mediaId"
        val room = MediaRoom(
            roomId = roomId,
            mediaId = mediaId,
            mediaType = mediaType
        )

        database.reference
            .child(ROOMS_REF)
            .child(roomId)
            .setValue(room)
            .await()

        Result.success(room)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getRoom(mediaId: Int, mediaType: String): Result<MediaRoom?> {
        return try {
            val roomId = "${mediaType}_$mediaId"
            val snapshot = database.reference
                .child(ROOMS_REF)
                .child(roomId)
                .get()
                .await()

            if (!snapshot.exists()) return Result.success(null)

            // Manually parse MediaRoom
            val roomIdValue = snapshot.child("roomId").getValue(String::class.java) ?: ""
            val mediaIdValue = snapshot.child("mediaId").getValue(Int::class.java) ?: 0
            val mediaTypeValue = snapshot.child("mediaType").getValue(String::class.java) ?: MediaType.MOVIE.name

            val messagesSnapshot = snapshot.child("messages")
            val messages = mutableListOf<Message>()

            for (messageSnapshot in messagesSnapshot.children) {
                val id = messageSnapshot.child("id").getValue(String::class.java) ?: ""
                val sender = messageSnapshot.child("senderName").getValue(String::class.java) ?: ""
                val text = messageSnapshot.child("text").getValue(String::class.java) ?: ""
                val timestamp = messageSnapshot.child("timestamp").getValue(Long::class.java) ?: 0L
                val senderId = messageSnapshot.child("senderId").getValue(String::class.java) ?: ""
                val profilePicture = messageSnapshot.child("senderProfilePicture").getValue(String::class.java) ?: ""
                val likes = messageSnapshot.child("likes").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
                messages.add(Message(id, sender,senderId, profilePicture, likes.toMutableList() ,text, timestamp, ))
            }

            val room = MediaRoom(roomIdValue, mediaIdValue, mediaTypeValue, messages)
            Result.success(room)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun addMessage(roomId: String, message: Message): Result<Boolean> {
        return try {
            if (!validateMessage(message)) {
                return Result.failure(IllegalArgumentException("Invalid message data"))
            }
            val messageRef = database.reference
                .child(ROOMS_REF)
                .child(roomId)
                .child(MESSAGES_REF)
                .child(message.id)

            messageRef.setValue(message).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editMessage(
        roomId: String,
        messageId: String,
        newText: String
    ): Result<Boolean> = try {
        val updates = hashMapOf<String, Any>(
            "text" to newText
        )

        database.reference
            .child(ROOMS_REF)
            .child(roomId)
            .child(MESSAGES_REF)
            .child(messageId)
            .updateChildren(updates)
            .await()

        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteMessage(roomId: String, messageId: String): Result<Boolean> = try {
        database.reference
            .child(ROOMS_REF)
            .child(roomId)
            .child(MESSAGES_REF)
            .child(messageId)
            .removeValue()
            .await()

        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun listenToRoomMessages(
        roomId: String,
    ): Flow<Resource<List<Message>>> = callbackFlow {
        trySendBlocking(Resource.Loading(isLoading = true))

        val reference = database.reference
            .child(ROOMS_REF)
            .child(roomId)
            .child(MESSAGES_REF)
            .orderByChild("timestamp")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull {
                    it.getValue(Message::class.java)
                }
                println("messages: $messages")
                trySendBlocking(Resource.Success(messages))
            }

            override fun onCancelled(error: DatabaseError) {
                trySendBlocking(Resource.Error(message = error.message))
            }
        }
        reference.addValueEventListener(listener)

        awaitClose {
            reference.removeEventListener(listener)
        }
    }

    override suspend fun addReaction(
        roomId: String,
        messageId: String,
        emoji: String
    ): Result<Boolean> = try {
        val messageRef = database.reference
            .child(ROOMS_REF)
            .child(roomId)
            .child(MESSAGES_REF)
            .child(messageId)
            .child("reactions")

        val snapshot = messageRef.get().await()
        val currentReactions = snapshot.getValue<Map<String, Int>>() ?: emptyMap()

        val updatedCount = (currentReactions[emoji] ?: 0) + 1
        messageRef.child(emoji).setValue(updatedCount).await()

        Result.success(true)
    } catch (e: Exception) {
        Result.failure(e)
    }

    private fun validateMessage(message: Message): Boolean {
        return message.id.isNotBlank() &&
                message.text.isNotBlank() &&
                (message.senderName.isNotEmpty())
    }

    override suspend fun toggleLikeMessage(
        userId: String,
        roomId: String,
        messageId: String,
    ): Result<Boolean> {
        return try {
            val messageRef = database.reference
                .child(ROOMS_REF)
                .child(roomId)
                .child(MESSAGES_REF)
                .child(messageId)

            val snapshot = messageRef.get().await()
            val likes = snapshot.child("likes").getValue(object : GenericTypeIndicator<List<String>>() {}) ?: emptyList()
            if(likes.contains(userId)) {
                messageRef.child("likes").setValue(likes.filter { it != userId})
            } else {
                messageRef.child("likes").setValue(likes + listOf(userId))
            }
            return Result.success(true)

        } catch (e : Exception) {
            Result.failure(e)
        }
    }
}

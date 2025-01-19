package com.example.moviesapp.presentation.mediadetails_screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.domain.model.Credits
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Message
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.model.UserData
import com.example.moviesapp.domain.repository.MediaDetailsRepository
import com.example.moviesapp.domain.repository.UserListRepository
import com.example.moviesapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class MediaDetailsViewModel @Inject constructor(
    private val mediaDetailsRepository: MediaDetailsRepository,
    private val repository: UserListRepository,
    private val savedStateHandle: SavedStateHandle,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaDetailsUiState())

    private var roomId: String? = null
    val mediaId = savedStateHandle.get<String>("mediaId")?.toInt()
    val mediaType = savedStateHandle.get<String>("mediaType")

    private val _movieRecommendationsState = MutableStateFlow<List<Movie>>(emptyList())
    private val _creditsState = MutableStateFlow<Credits?>(null)
    private val _seriesRecommendationsState = MutableStateFlow<List<Series>>(emptyList())
    private val _mediaRoomMessages = MutableStateFlow<List<Message>>(emptyList())
    val mediaRoomMessage = _mediaRoomMessages.asStateFlow()


    private val currentUser = auth.currentUser
    val selectedListIds = MutableStateFlow<List<String>>(emptyList())
    private val videoUrl = MutableStateFlow<String?>(null)
    var selectedMessageId = MutableStateFlow<String?>(null)
        private set

    fun selectMessage(messageId: String?) {
        selectedMessageId.value = messageId
    }

    val mediaDetailsUiState = _uiState
        .combine(_movieRecommendationsState) { state, movieRecs ->
            state.copy(movieRecommendations = movieRecs)
        }
        .combine(_seriesRecommendationsState) { state, seriesRecs ->
            state.copy(seriesRecommendations = seriesRecs)
        }
        .combine(_creditsState) { state, creds ->
            state.copy(credits = creds)
        }
        .combine(videoUrl) { state, url ->
            state.copy(videoUrl = url)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MediaDetailsUiState()
        )


    init {
        if (mediaId != null && mediaType != null) {
            loadMediaDetails(mediaId, mediaType, fetchFromRemote = true)
            viewModelScope.launch(Dispatchers.IO) {
                val videoResult = async { fetchMediaVideo(mediaId, mediaType) }
                val recommendationsResult = async { fetchMediaRecommendations(mediaId, mediaType) }
                val creditsResult =
                    async { fetchCreditsDetails(MediaType.valueOf(mediaType), mediaId) }

                videoResult.await()
                recommendationsResult.await()
                creditsResult.await()

            }
            fetchAndLoadMessages()
        }
    }


    fun OnEvent(event: MediaDetailsEvent) {
        when (event) {
            is MediaDetailsEvent.OnAddClick -> {
                println("selectedListIds: ${selectedListIds.value}")
                selectedListIds.value.forEach { id ->
                    println("id: $id")
                    val media = Media(
                        id = mediaId!!,
                        title = if (mediaType == MediaType.MOVIE.name) _uiState.value.movieDetails?.title
                            ?: ""
                        else _uiState.value.seriesDetails?.title ?: "",
                        posterPath = if (mediaType == MediaType.MOVIE.name) _uiState.value.movieDetails?.posterPath
                            ?: ""
                        else _uiState.value.seriesDetails?.posterPath ?: "",
                        type = mediaType!!
                    )

                    currentUser?.let {
                        addMediaToWatchListByWatchListId(id, media, it.uid)
                    }
                }
            }

            is MediaDetailsEvent.OnRefresh -> {
                _uiState.update {
                    it.copy(isRefreshing = true)
                }
                if (mediaId != null && mediaType != null) {
                    loadMediaDetails(mediaId, mediaType, fetchFromRemote = true)
                    viewModelScope.launch(Dispatchers.IO) {
                        val videoResult = async { fetchMediaVideo(mediaId, mediaType) }
                        val recommendationsResult =
                            async { fetchMediaRecommendations(mediaId, mediaType) }
                        val creditsResult =
                            async { fetchCreditsDetails(MediaType.valueOf(mediaType), mediaId) }
                        videoResult.await()
                        recommendationsResult.await()
                        creditsResult.await()

                        _uiState.update {
                            it.copy(isRefreshing = false)
                        }

                    }
                }


            }

            is MediaDetailsEvent.OnAddReaction -> {
                addReaction(event.messageId, event.emoji)
            }

            is MediaDetailsEvent.OnDeleteMessage -> {
                deleteMessage(event.messageId)

            }

            is MediaDetailsEvent.OnRemoveReaction -> Unit
            is MediaDetailsEvent.OnSendMessage -> {
                sendMessage(event.message, event.userData)
            }

            is MediaDetailsEvent.OnUpdateMessage -> TODO()
            is MediaDetailsEvent.OnLikedButtonClicked -> {
                updateLike(event.messageId)
            }

            is MediaDetailsEvent.OnAddToWatchList -> TODO()
            is MediaDetailsEvent.OnCreateWatchList -> {
                addWatchList(event.watchListName, userId = currentUser?.uid ?: "")
            }
        }

    }

    private fun loadMediaDetails(
        mediaId: Int,
        mediaType: String,
        fetchFromRemote: Boolean = false
    ) {
        when (mediaType) {
            "MOVIE" -> fetchMovieDetails(mediaId, fetchFormRemote = fetchFromRemote)
            "SERIES" -> fetchSeriesDetails(mediaId, fetchFormRemote = fetchFromRemote)
        }
    }

    private fun fetchMediaVideo(mediaId: Int, mediaType: String) {
        when (mediaType) {
            "MOVIE" -> getMovieVideo(mediaId)
            "SERIES" -> getSeriesVideo(mediaId)
        }

    }

    private fun fetchMediaRecommendations(mediaId: Int, mediaType: String) {
        when (mediaType) {
            "MOVIE" -> getMoviesRecommendations(mediaId, 1)
            "SERIES" -> getSeriesRecommendations(mediaId, 1)
        }
    }

    private fun fetchMovieDetails(
        movieId: Int,
        language: String = "en-US",
        fetchFormRemote: Boolean = false
    ) {

        viewModelScope.launch {
            mediaDetailsRepository.getMovieDetailsById(
                movieId = movieId,
                language = language,
                fetchFormRemote = fetchFormRemote
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            movieDetails = result.data,
                            seriesDetails = null,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            movieDetails = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            movieDetails = null,
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }


        }


    }

    private fun getMovieVideo(movieId: Int) {
        viewModelScope.launch {
            when (val result = mediaDetailsRepository.getMovieVideoById(movieId)) {
                is Resource.Success -> {
                    videoUrl.update {
                        result.data
                    }
                }

                is Resource.Error -> {
                    videoUrl.value = null
                }

                is Resource.Loading -> {
                    videoUrl.value = null
                }
            }

        }


    }

    private fun getMoviesRecommendations(movieId: Int, page: Int) {
        viewModelScope.launch {
            when (val result = mediaDetailsRepository.getMovieRecommendations(movieId, page)) {
                is Resource.Success -> {
                    _movieRecommendationsState.update { currentList ->
                        result.data ?: currentList
                    }
                }

                is Resource.Error -> {
                    _movieRecommendationsState.value = emptyList()
                }

                is Resource.Loading -> {
                    _movieRecommendationsState.value = emptyList()
                }
            }
        }
    }

    private fun fetchSeriesDetails(
        seriesId: Int,
        language: String = "en-US",
        fetchFormRemote: Boolean = false
    ) {
        viewModelScope.launch {
            mediaDetailsRepository.getSeriesDetailsById(
                seriesId = seriesId,
                language = language,
                fetchFormRemote = fetchFormRemote
            ).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _uiState.value = _uiState.value.copy(
                            seriesDetails = result.data,
                            movieDetails = null,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _uiState.value = _uiState.value.copy(
                            seriesDetails = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    is Resource.Loading -> {
                        _uiState.value = _uiState.value.copy(
                            seriesDetails = null,
                            isLoading = true,
                            error = null
                        )
                    }
                }

            }
        }
    }

    private fun getSeriesVideo(seriesId: Int) {
        viewModelScope.launch {
            when (val result = mediaDetailsRepository.getSeriesVideoById(seriesId)) {
                is Resource.Success -> {
                    videoUrl.update {
                        result.data
                    }
                }

                is Resource.Error -> {
                    videoUrl.value = null
                }

                is Resource.Loading -> {
                    videoUrl.value = null
                }
            }

        }

    }

    private fun getSeriesRecommendations(seriesId: Int, page: Int) {
        viewModelScope.launch {
            when (val result = mediaDetailsRepository.getSeriesRecommendations(seriesId, page)) {
                is Resource.Success -> {
                    _seriesRecommendationsState.value = result.data ?: emptyList()
                }

                is Resource.Error -> {
                    _seriesRecommendationsState.value = emptyList()
                }

                is Resource.Loading -> {
                    _seriesRecommendationsState.value = emptyList()
                }
            }
        }

    }

    private fun fetchCreditsDetails(
        type: MediaType,
        mediaId: Int,
    ) {
        viewModelScope.launch {
            mediaDetailsRepository.getCreditsDetails(type, mediaId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _creditsState.value = result.data
                    }

                    is Resource.Error -> {
                        _creditsState.value = null
                    }

                    is Resource.Loading -> {
                        _creditsState.value = null

                    }


                }


            }
        }

    }

    private fun addMediaToWatchListByWatchListId(
        watchListId: String,
        media: Media,
        userId: String
    ) {
        viewModelScope.launch {
            repository.addMediaToListByListId(watchListId, media, userId)
        }

    }

    private fun addWatchList(
        watchListName: String,
        userId: String
    ) {
        viewModelScope.launch {
            repository.addWatchList(userId, watchListName)
        }
    }

    private fun fetchAndLoadMessages() {
        viewModelScope.launch {
            try {
                val room = mediaDetailsRepository.getRoom(mediaId!!, mediaType!!).getOrThrow()
                roomId = room?.roomId
                if (room == null) {
                    val newRoom = mediaDetailsRepository.createRoom(mediaId, mediaType).getOrThrow()
                    roomId = newRoom.roomId
                }
                roomId?.let { id ->
                    mediaDetailsRepository.listenToRoomMessages(id).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _mediaRoomMessages.update {
                                    result.data ?: emptyList()
                                }

                            }

                            is Resource.Error -> {
                                _mediaRoomMessages.update {
                                    emptyList()
                                }

                            }

                            is Resource.Loading -> {
                                _mediaRoomMessages.update {
                                    emptyList()
                                }

                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error fetching room: ${e.message}")
                _mediaRoomMessages.update {
                    emptyList()
                }
            }
        }
    }

    private fun sendMessage(text: String, userData: UserData) {
        viewModelScope.launch {
            try {
                roomId?.let { id ->
                    val message = Message(
                        id = UUID.randomUUID().toString(),
                        senderName = userData.username,
                        senderId = userData.userId,
                        senderProfilePicture = userData.profilePictureUrl,
                        text = text,
                        timestamp = System.currentTimeMillis()
                    )
                    mediaDetailsRepository.addMessage(id, message)
                }
            } catch (e: Exception) {
                Log.e("MediaDetailsViewModel", "Error sending message", e)
            }
        }
    }

    private fun addReaction(messageId: String, emoji: String) {
        viewModelScope.launch {
            try {
                roomId?.let { id ->
                    mediaDetailsRepository.addReaction(id, messageId, emoji)
                }
            } catch (e: Exception) {
                Log.e("MediaDetailsViewModel", "Error adding reaction", e)
            }
        }

    }

    private fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            try {
                roomId?.let {  id->
                    mediaDetailsRepository.deleteMessage(id, messageId)
                }
            } catch (e: Exception) {
                Log.e("MediaDetailsViewModel", "Error deleting msg", e)
            }
        }
    }

    private fun updateLike(messageId: String) {
        viewModelScope.launch {
            try {
                roomId?.let {  id->
                    currentUser?.uid?.let { mediaDetailsRepository.toggleLikeMessage(it, id, messageId) }
                }
            } catch (e: Exception) {
                Log.e("MediaDetailsViewModel", "Error deleting msg", e)
            }
        }
    }
}


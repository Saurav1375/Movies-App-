package com.example.moviesapp.presentation.mediadetails_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.domain.model.Credits
import com.example.moviesapp.domain.model.Media
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series
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
import javax.inject.Inject


@HiltViewModel
class MediaDetailsViewModel @Inject constructor(
    private val mediaDetailsRepository: MediaDetailsRepository,
    private val repository: UserListRepository,
    private val savedStateHandle: SavedStateHandle,
    private val auth : FirebaseAuth
) : ViewModel() {

    private val _mediaDetailsState = MutableStateFlow(MediaDetailsState())
    private val _movieRecommendationsState = MutableStateFlow<List<Movie>>(emptyList())
    private val _creditsState = MutableStateFlow<Credits?>(null)
    private val _seriesRecommendationsState = MutableStateFlow<List<Series>>(emptyList())
    private val currentUser = auth.currentUser
    val selectedListIds = MutableStateFlow<List<String>>(emptyList())
//    val selectedListIds = _selectedListIds.asStateFlow()

    private val videoUrl = MutableStateFlow<String?>(null)

    val mediaDetailsState = combine(
        _mediaDetailsState,
        videoUrl,
        _movieRecommendationsState,
        _seriesRecommendationsState,
        _creditsState

    ) { state, videoUrl, movieRecommendations, seriesRecommendations, credits ->
        state.copy(
            videoUrl = videoUrl,
            movieRecommendations = movieRecommendations,
            seriesRecommendations = seriesRecommendations,
            credits = credits

        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        MediaDetailsState()
    )

    val mediaId = savedStateHandle.get<String>("mediaId")?.toInt()
    val mediaType = savedStateHandle.get<String>("mediaType")

    init {
        if (mediaId != null && mediaType != null) {
            fetchMediaDetails(mediaId, mediaType, fetchFromRemote = true)
            viewModelScope.launch(Dispatchers.IO) {
                val videoResult = async { fetchMediaVideo(mediaId, mediaType) }
                val recommendationsResult = async { fetchMediaRecommendations(mediaId, mediaType) }
                val creditsResult =
                    async { fetchCreditsDetails(MediaType.valueOf(mediaType), mediaId) }
                videoResult.await()
                recommendationsResult.await()
                creditsResult.await()

            }
        }
    }

    fun OnEvent(event: MediaDetailsEvent) {
        when (event) {
            is MediaDetailsEvent.OnAddClick -> {
                selectedListIds.value.forEach { id ->
                    println("id: $id")
                    val media = Media(
                        id = mediaId!!,
                        title = if (mediaType == MediaType.MOVIE.name) _mediaDetailsState.value.movieDetails?.title ?: ""
                                    else _mediaDetailsState.value.seriesDetails?.title ?: "",
                        posterPath = if (mediaType == MediaType.MOVIE.name) _mediaDetailsState.value.movieDetails?.posterPath ?: ""
                                        else _mediaDetailsState.value.seriesDetails?.posterPath ?: "",
                        type = mediaType!!
                    )

                    currentUser?.let {
                        addMediaToWatchListByWatchListId(id, media, it.uid )
                    }
                }
            }

            is MediaDetailsEvent.OnRefresh -> {
                _mediaDetailsState.update {
                    it.copy(isRefreshing = true)
                }
                if (mediaId != null && mediaType != null) {
                    fetchMediaDetails(mediaId, mediaType, fetchFromRemote = true)
                    viewModelScope.launch(Dispatchers.IO) {
                        val videoResult = async { fetchMediaVideo(mediaId, mediaType) }
                        val recommendationsResult = async { fetchMediaRecommendations(mediaId, mediaType) }
                        val creditsResult =
                            async { fetchCreditsDetails(MediaType.valueOf(mediaType), mediaId) }
                        videoResult.await()
                        recommendationsResult.await()
                        creditsResult.await()

                        _mediaDetailsState.update {
                            it.copy(isRefreshing = false)
                        }

                    }
                }


            }
        }

    }

    private fun fetchMediaDetails(mediaId: Int, mediaType: String, fetchFromRemote: Boolean = false) {
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
                        _mediaDetailsState.value = mediaDetailsState.value.copy(
                            movieDetails = result.data,
                            seriesDetails = null,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _mediaDetailsState.value = mediaDetailsState.value.copy(
                            movieDetails = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    is Resource.Loading -> {
                        _mediaDetailsState.value = mediaDetailsState.value.copy(
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
                        _mediaDetailsState.value = mediaDetailsState.value.copy(
                            seriesDetails = result.data,
                            movieDetails = null,
                            isLoading = false,
                            error = null
                        )
                    }

                    is Resource.Error -> {
                        _mediaDetailsState.value = mediaDetailsState.value.copy(
                            seriesDetails = null,
                            isLoading = false,
                            error = result.message
                        )
                    }

                    is Resource.Loading -> {
                        _mediaDetailsState.value = mediaDetailsState.value.copy(
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

    private fun addMediaToWatchListByWatchListId(watchListId: String, media: Media, userId: String) {
        viewModelScope.launch {
            repository.addMediaToListByListId(watchListId, media, userId)
        }

    }
}
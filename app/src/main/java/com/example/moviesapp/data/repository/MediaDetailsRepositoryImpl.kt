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
import com.example.moviesapp.domain.model.MediaType
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieDetails
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.model.SeriesDetails
import com.example.moviesapp.domain.repository.MediaDetailsRepository
import com.example.moviesapp.utils.Resource
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class MediaDetailsRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val db: MovieDatabase
) : MediaDetailsRepository {

    private val movieDetailsDao = db.movieDetailsDao
    private val seriesDetailsDao = db.seriesDetailsDao

    override fun getMovieDetailsById(
        language: String,
        movieId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<MovieDetails>> = flow {
        emit(Resource.Loading())

        if (fetchFormRemote) {
            try {
                val response = api.getMovieDetails(movieId, language)
                if (response.isSuccessful) {
                    response.body()?.let { movieDetailsDto ->
                        val movieDetailsEntity = movieDetailsDto.toMovieDetailsEntity()
                        movieDetailsDao.deleteMovieDetailsById(movieDetailsEntity.id)
                        movieDetailsDao.insertMovieDetails(listOf(movieDetailsEntity))
                        emit(Resource.Success(movieDetailsEntity.toMovieDetails()))
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
        } else {
            val movieDetailsEntity = movieDetailsDao.getMovieDetailsById(movieId)
            if (movieDetailsEntity != null) {
                emit(Resource.Success(movieDetailsEntity.toMovieDetails()))
            }

        }

    }

    override fun getSeriesDetailsById(
        language: String,
        seriesId: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<SeriesDetails>> = flow {
        emit(Resource.Loading())
        if (fetchFormRemote) {
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
        } else {
            val seriesDetailsEntity = seriesDetailsDao.getSeriesDetailsById(seriesId)
            if (seriesDetailsEntity != null) {
                emit(Resource.Success(seriesDetailsEntity.toSeriesDetails()))
            }
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

    override suspend fun getSeriesRecommendations(seriesId: Int, page: Int): Resource<List<Series>> {
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
}

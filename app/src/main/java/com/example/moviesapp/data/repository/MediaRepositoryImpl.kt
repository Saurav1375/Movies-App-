package com.example.moviesapp.data.repository

import android.util.Log
import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.mapper.toMovie
import com.example.moviesapp.data.mapper.toMovieEntity
import com.example.moviesapp.data.mapper.toSeries
import com.example.moviesapp.data.mapper.toSeriesEntity
import com.example.moviesapp.data.remote.ApiService
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.repository.MediaRepository
import com.example.moviesapp.utils.Resource
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

class MediaRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val db: MovieDatabase
) : MediaRepository {
    private val movieDao = db.movieDao
    private val seriesDao = db.seriesDao
    override fun getMoviesByType(
        language: String,
        type: String,
        page : Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())

        if (fetchFormRemote) {
            try {
                val response = when (type) {
                    "now_playing" -> api.getNowPlayingMovies(language = language, page = page)
                    "upcoming" -> api.getUpcomingMovies(language = language, page = page)
                    "popular" -> api.getPopularMovies(language = language, page = page)
                    "top_rated" -> api.getTopRatedMovies(language = language, page = page)
                    else -> api.getNowPlayingMovies(language = language, page = page)
                }

                if (response.isSuccessful) {
                    response.body()?.let { movieDto ->
                        movieDao.clearMoviesByType(type)
                        movieDao.upsertAll(movieDto.toMovieEntity(type))
                        emit(Resource.Success(movieDao.getMoviesByType(type).map { it.toMovie() }))
                    }
                }
            } catch (e: IOException) {
                Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
                emit(Resource.Error(message = "Couldn't load data"))

            } catch (e : HttpException) {
                Log.e("MovieRepositoryImpl", "Exception: ${e.message}")

                emit(Resource.Error(message = "Couldn't load data"))

            } catch (e : Exception) {
                Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
                emit(Resource.Error(message = "Couldn't load data"))
            }
        }
        else{
            emit(Resource.Success(movieDao.getMoviesByType(type).map { it.toMovie() }))
        }

    }

    override fun getSeriesByType(
        language: String,
        type: String,
        page: Int,
        fetchFormRemote: Boolean
    ): Flow<Resource<List<Series>>> = flow {
        emit(Resource.Loading())

        if (fetchFormRemote) {
            try {
                val response = when (type) {
                    "airing_today" -> api.getAiringTodaySeries(language = language, page = page)
                    "on_the_air" -> api.getOnTheAirSeries(language = language, page = page)
                    "popular" -> api.getPopularSeries(language = language, page = page)
                    "top_rated" -> api.getTopRatedSeries(language = language, page = page)
                    else -> api.getAiringTodaySeries(language = language, page = page)

                }

                if (response.isSuccessful) {
                    response.body()?.let { seriesDto ->
                        seriesDao.clearSeriesByType(type)
                        seriesDao.upsertAll(seriesDto.toSeriesEntity(type))
                        emit(Resource.Success(seriesDao.getSeriesByType(type).map { it.toSeries() }))

                    }
                }
            } catch (e: IOException) {
                Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
                emit(Resource.Error(message = "Couldn't load data"))

            } catch (e : HttpException) {
                Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
                emit(Resource.Error(message = "Couldn't load data"))

            } catch (e : Exception) {
                Log.e("MovieRepositoryImpl", "Exception: ${e.message}")
                emit(Resource.Error(message = "Couldn't load data"))
            }
        }
        else{
            emit(Resource.Success(seriesDao.getSeriesByType(type).map { it.toSeries() }))

        }

    }
}
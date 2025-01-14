package com.example.moviesapp.data.repository

import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.mapper.toMovie
import com.example.moviesapp.data.mapper.toMovieEntity
import com.example.moviesapp.data.mapper.toSeries
import com.example.moviesapp.data.mapper.toSeriesEntity
import com.example.moviesapp.data.remote.ApiService
import com.example.moviesapp.domain.model.Movie
import com.example.moviesapp.domain.model.MovieGenre
import com.example.moviesapp.domain.model.Series
import com.example.moviesapp.domain.model.SeriesGenre
import com.example.moviesapp.domain.repository.WatchGuideRepository
import com.example.moviesapp.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class WatchGuideRepositoryImpl @Inject constructor(
    private val api: ApiService,
    private val db: MovieDatabase
) : WatchGuideRepository {
    private val movieDao = db.movieDao
    private val seriesDao = db.seriesDao

    override fun getMoviesByGenre(
        genreId: Int,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean
    ): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())

        val genreName = MovieGenre.fromId(genreId)?.name
        if (genreName == null) {
            emit(Resource.Error("Invalid genre ID"))
            return@flow
        }
        val localMovies = movieDao.getMoviesByType(genreName)
        emit(Resource.Success(localMovies.map { it.toMovie() }))

        val shouldLoadFromCache = localMovies.isNotEmpty() && !fetchFromRemote

        if (shouldLoadFromCache) {
            emit(Resource.Loading(false))
            return@flow
        }

        try {
            val response = api.getMoviesByGenre(
                genreId = genreId,
                includeAdult = includeAdult,
                page = page,
                language = language
            )

            if (response.isSuccessful && response.body() != null) {
                val movies = response.body()!!.toMovieEntity(type = genreName)
                movieDao.clearMoviesByType(genreName)
                movieDao.upsertAll(movies)

                val updatedMovies = movieDao.getMoviesByType(genreName).map { it.toMovie() }
                emit(Resource.Success(updatedMovies))
            } else {
                emit(Resource.Error("Failed to fetch movies. Response code: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    override fun getSeriesByGenre(
        genreId: Int,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean
    ): Flow<Resource<List<Series>>> = flow {
        emit(Resource.Loading())

        val genreName = SeriesGenre.fromId(genreId)?.name
        if (genreName == null) {
            emit(Resource.Error("Invalid genre ID"))
            return@flow
        }

        val localSeries = seriesDao.getSeriesByType(genreName)
        emit(Resource.Success(localSeries.map { it.toSeries() }))
        val shouldLoadFromCache = localSeries.isNotEmpty() && !fetchFromRemote

        if (shouldLoadFromCache) {
            emit(Resource.Loading(false))
            return@flow
        }

        try {
            val response = api.getTVByGenre(
                genreId = genreId,
                includeAdult = includeAdult,
                page = page,
                language = language
            )

            if (response.isSuccessful && response.body() != null) {
                val series = response.body()!!.toSeriesEntity(type = genreName)
                seriesDao.clearSeriesByType(genreName)
                seriesDao.upsertAll(series)

                val updatedSeries = seriesDao.getSeriesByType(genreName).map { it.toSeries() }
                emit(Resource.Success(updatedSeries))
            } else {
                emit(Resource.Error("Failed to fetch series. Response code: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.flowOn(Dispatchers.IO)


    override fun getMoviesByQuery(
        query: String,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean,
        year: Int
    ): Flow<Resource<List<Movie>>> {
        TODO("Not yet implemented")
    }

    override fun getSeriesByQuery(
        query: String,
        page: Int,
        fetchFromRemote: Boolean,
        language: String,
        includeAdult: Boolean,
        year: Int
    ): Flow<Resource<List<Series>>> {
        TODO("Not yet implemented")
    }
}
package com.example.moviesapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.moviesapp.data.local.MovieDatabase
import com.example.moviesapp.data.mapper.toSearchMovie
import com.example.moviesapp.data.mapper.toSearchMovieEntity
import com.example.moviesapp.data.remote.ApiService
import com.example.moviesapp.domain.model.SearchMovie

class MoviePagingSource(
    private val api: ApiService,
    private val db: MovieDatabase,
    private val query: String,
    private val language: String,
    private val isAdult: Boolean,
) : PagingSource<Int, SearchMovie>() {

    override fun getRefreshKey(state: PagingState<Int, SearchMovie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchMovie> {
        return try {
            val page = params.key ?: 1
            val response = api.searchMovies(
                query = query,
                page = page,
                language = language,
                includeAdult = isAdult,
            )

            response.body()?.let {
                db.searchMovieDao.insertMovies(
                    it.toSearchMovieEntity(query, page)
                )
            }

            LoadResult.Page(
                data = response.body()?.toSearchMovie() ?: emptyList(),
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (page < (response.body()?.totalPages ?: 2)) page + 1 else null
            )
        } catch (e: Exception) {
            //load from cache if network request fails
            try {

                val cachedMovies = db.searchMovieDao.getMovies(
                    searchQuery = query,
                    limit = params.loadSize,
                    offset = ((params.key ?: 1) - 1) * params.loadSize
                )

                if (cachedMovies.isNotEmpty()) {
                    LoadResult.Page(
                        data = cachedMovies.map { it.toSearchMovie() },
                        prevKey = if (params.key == 1) null else params.key?.minus(1),
                        nextKey = params.key?.plus(1)
                    )
                } else {
                    LoadResult.Error(e)
                }
            } catch (e: Exception) {
                LoadResult.Error(e)
            }
        }
    }
}
package com.gianzra.expert.core.data.source.local

import com.gianzra.expert.core.data.source.local.entity.MovieEntity
import com.gianzra.expert.core.data.source.local.room.MovieDao
import com.gianzra.expert.core.utils.SortUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn

class LocalDataSource(private val mMovieDao: MovieDao) {

    fun getAllMovies(sort: String): Flow<List<MovieEntity>> =
        mMovieDao.getMovies(SortUtils.getSortedQueryMovies(sort))

    fun getAllTvShows(sort: String): Flow<List<MovieEntity>> =
        mMovieDao.getTvShows(SortUtils.getSortedQueryTvShows(sort))

    fun getAllFavoriteMovies(sort: String): Flow<List<MovieEntity>> =
        mMovieDao.getFavoriteMovies(SortUtils.getSortedQueryFavoriteMovies(sort))

    fun getAllFavoriteTvShows(sort: String): Flow<List<MovieEntity>> =
        mMovieDao.getFavoriteTvShows(SortUtils.getSortedQueryFavoriteTvShows(sort))

    fun getMovieSearch(search: String): Flow<List<MovieEntity>> =
        mMovieDao.getSearchMovies(search)
            .flowOn(Dispatchers.Default)
            .conflate()

    fun getTvShowSearch(search: String): Flow<List<MovieEntity>> =
        mMovieDao.getSearchTvShows(search)
            .flowOn(Dispatchers.Default)
            .conflate()

    suspend fun insertMovies(movies: List<MovieEntity>) {
        mMovieDao.insertMovie(movies)
    }

    fun setMovieFavorite(movie: MovieEntity, newState: Boolean) {
        val updatedMovie = movie.copy(favorite = newState)
        mMovieDao.updateFavoriteMovie(updatedMovie)
    }
}
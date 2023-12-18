package com.gianzra.expert.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.domain.usecase.MovieAppUseCase

class FavoriteViewModel(private val movieAppUseCase: MovieAppUseCase) : ViewModel() {

    fun getFavoriteMovies(sort: String): LiveData<List<Movie>> {
        return movieAppUseCase.getFavoriteMovies(sort).asLiveData()
    }

    fun getFavoriteTvShows(sort: String): LiveData<List<Movie>> {
        return movieAppUseCase.getFavoriteTvShows(sort).asLiveData()
    }

    fun setFavorite(movie: Movie, newState: Boolean) {
        movieAppUseCase.setMovieFavorite(movie, newState)
    }
}

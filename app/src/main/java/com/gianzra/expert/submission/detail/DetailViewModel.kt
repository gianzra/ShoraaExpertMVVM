package com.gianzra.expert.submission.detail

import androidx.lifecycle.ViewModel
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.domain.usecase.MovieAppUseCase

class DetailViewModel(private val movieAppUseCase: MovieAppUseCase) : ViewModel() {

    fun setFavoriteMovie(movie: Movie, newStatus: Boolean) {
        movieAppUseCase.setMovieFavorite(movie, newStatus)
    }
}

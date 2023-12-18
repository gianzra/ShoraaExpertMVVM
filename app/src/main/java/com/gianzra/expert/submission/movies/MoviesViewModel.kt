package com.gianzra.expert.submission.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.gianzra.expert.core.data.Resource
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.domain.usecase.MovieAppUseCase

class MoviesViewModel(private val movieAppUseCase: MovieAppUseCase) : ViewModel() {

    fun getMovies(sort: String): LiveData<Resource<List<Movie>>> {
        val moviesFlow = movieAppUseCase.getAllMovies(sort)
        return moviesFlow.asLiveData()
    }
}

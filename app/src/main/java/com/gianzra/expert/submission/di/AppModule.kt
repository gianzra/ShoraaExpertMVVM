package com.gianzra.expert.submission.di

import com.gianzra.expert.core.domain.usecase.MovieAppInteractor
import com.gianzra.expert.core.domain.usecase.MovieAppUseCase
import com.gianzra.expert.submission.detail.DetailViewModel
import com.gianzra.expert.submission.home.SearchViewModel
import com.gianzra.expert.submission.movies.MoviesViewModel
import com.gianzra.expert.submission.tvshows.TvShowsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val useCaseModule = module {
    factory<MovieAppUseCase> { MovieAppInteractor(get()) }
}

@ExperimentalCoroutinesApi
@FlowPreview
val viewModelModule = module {
    viewModel { MoviesViewModel(get()) }
    viewModel { TvShowsViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { SearchViewModel() }
}
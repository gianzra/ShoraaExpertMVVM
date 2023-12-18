package com.gianzra.expert.core.utils

import com.gianzra.expert.core.data.source.local.entity.MovieEntity
import com.gianzra.expert.core.data.source.remote.response.MovieResponse
import com.gianzra.expert.core.data.source.remote.response.TvShowResponse
import com.gianzra.expert.core.domain.model.Movie

object DataMapper {
    fun mapMovieResponsesToEntities(input: List<MovieResponse>): List<MovieEntity> =
        input.map {
            MovieEntity(
                it.overview,
                it.originalLanguage,
                it.releaseDate,
                it.popularity,
                it.voteAverage,
                it.id,
                it.title,
                it.voteCount,
                it.posterPath,
                favorite = false,
                isTvShows = false
            )
        }

    fun mapTvShowResponsesToEntities(input: List<TvShowResponse>): List<MovieEntity> =
        input.map {
            MovieEntity(
                it.overview,
                it.originalLanguage,
                it.firstAirDate,
                it.popularity,
                it.voteAverage,
                it.id,
                it.name,
                it.voteCount,
                it.posterPath,
                favorite = false,
                isTvShows = true
            )
        }

    fun mapEntitiesToDomain(input: List<MovieEntity>): List<Movie> =
        input.map {
            Movie(
                it.overview,
                it.originalLanguage,
                it.releaseDate,
                it.popularity,
                it.voteAverage,
                it.id,
                it.title,
                it.voteCount,
                it.posterPath,
                favorite = it.favorite,
                isTvShows = it.isTvShows
            )
        }

    fun mapDomainToEntity(input: Movie): MovieEntity =
        MovieEntity(
            input.overview,
            input.originalLanguage,
            input.releaseDate,
            input.popularity,
            input.voteAverage,
            input.id,
            input.title,
            input.voteCount,
            input.posterPath,
            favorite = input.favorite,
            isTvShows = input.isTvShows
        )
}

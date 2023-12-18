package com.gianzra.expert.core.utils

import androidx.sqlite.db.SimpleSQLiteQuery

object SortUtils {

    const val POPULARITY = "Popularity"
    const val VOTE = "Vote"
    const val NEWEST = "Newest"
    const val RANDOM = "Random"

    private const val BASE_QUERY = "SELECT * FROM movieEntities where isTvShow = "

    fun getSortedQueryMovies(filter: String): SimpleSQLiteQuery =
        SimpleSQLiteQuery("${BASE_QUERY}0 ${getOrderByClause(filter)}")

    fun getSortedQueryTvShows(filter: String): SimpleSQLiteQuery =
        SimpleSQLiteQuery("${BASE_QUERY}1 ${getOrderByClause(filter)}")

    fun getSortedQueryFavoriteMovies(filter: String): SimpleSQLiteQuery =
        SimpleSQLiteQuery("${BASE_QUERY}0 AND favorite = 1 ${getOrderByClause(filter)}")

    fun getSortedQueryFavoriteTvShows(filter: String): SimpleSQLiteQuery =
        SimpleSQLiteQuery("${BASE_QUERY}1 AND favorite = 1 ${getOrderByClause(filter)}")

    private fun getOrderByClause(filter: String): String = when (filter) {
        POPULARITY -> "ORDER BY popularity DESC"
        NEWEST -> "ORDER BY releaseDate DESC"
        VOTE -> "ORDER BY voteAverage DESC"
        RANDOM -> "ORDER BY RANDOM()"
        else -> ""
    }
}

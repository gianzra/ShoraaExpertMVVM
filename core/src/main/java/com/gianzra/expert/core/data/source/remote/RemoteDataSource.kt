package com.gianzra.expert.core.data.source.remote

import com.gianzra.expert.core.BuildConfig
import com.gianzra.expert.core.data.source.remote.network.ApiResponse
import com.gianzra.expert.core.data.source.remote.network.ApiService
import com.gianzra.expert.core.data.source.remote.response.MovieResponse
import com.gianzra.expert.core.data.source.remote.response.TvShowResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import java.lang.Exception

class RemoteDataSource(private val apiService: ApiService) {

    private val apiKey = BuildConfig.KEY

    private suspend fun <T> fetchData(apiCall: suspend () -> List<T>): Flow<ApiResponse<List<T>>> = flow {
        try {
            val response = apiCall()
            emit(if (response.isNotEmpty()) ApiResponse.Success(response) else ApiResponse.Empty)
        } catch (e: Exception) {
            emit(ApiResponse.Error(e.toString()))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getMovies(): Flow<ApiResponse<List<MovieResponse>>> = fetchData { apiService.getMovies(apiKey).results }

    suspend fun getTvShows(): Flow<ApiResponse<List<TvShowResponse>>> = fetchData { apiService.getTvShows(apiKey).results }
}

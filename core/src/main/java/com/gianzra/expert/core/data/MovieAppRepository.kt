package com.gianzra.expert.core.data

import com.gianzra.expert.core.data.source.local.LocalDataSource
import com.gianzra.expert.core.data.source.remote.RemoteDataSource
import com.gianzra.expert.core.data.source.remote.network.ApiResponse
import com.gianzra.expert.core.domain.model.Movie
import com.gianzra.expert.core.domain.repository.IMovieAppRepository
import com.gianzra.expert.core.utils.AppExecutors
import com.gianzra.expert.core.utils.DataMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MovieAppRepository(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val appExecutors: AppExecutors
) : IMovieAppRepository {

    override fun getAllMovies(sort: String): Flow<Resource<List<Movie>>> =
        createNetworkBoundResource(
            loadFromDB = { localDataSource.getAllMovies(sort).map { DataMapper.mapEntitiesToDomain(it) } },
            shouldFetch = { it.isNullOrEmpty() },
            createCall = { remoteDataSource.getMovies() },
            saveCallResult = { data ->
                withContext(Dispatchers.IO) {
                    val movieList = DataMapper.mapMovieResponsesToEntities(data)
                    localDataSource.insertMovies(movieList)
                }
            }
        )

    override fun getAllTvShows(sort: String): Flow<Resource<List<Movie>>> =
        createNetworkBoundResource(
            loadFromDB = { localDataSource.getAllTvShows(sort).map { DataMapper.mapEntitiesToDomain(it) } },
            shouldFetch = { it == null || it.isEmpty() },
            createCall = { remoteDataSource.getTvShows() },
            saveCallResult = { data ->
                withContext(Dispatchers.IO) {
                    val tvShowList = DataMapper.mapTvShowResponsesToEntities(data)
                    localDataSource.insertMovies(tvShowList)
                }
            }
        )

    override fun getSearchMovies(search: String): Flow<List<Movie>> =
        localDataSource.getMovieSearch(search).map { DataMapper.mapEntitiesToDomain(it) }

    override fun getSearchTvShows(search: String): Flow<List<Movie>> =
        localDataSource.getTvShowSearch(search).map { DataMapper.mapEntitiesToDomain(it) }

    override fun getFavoriteMovies(sort: String): Flow<List<Movie>> =
        localDataSource.getAllFavoriteMovies(sort).map { DataMapper.mapEntitiesToDomain(it) }

    override fun getFavoriteTvShows(sort: String): Flow<List<Movie>> =
        localDataSource.getAllFavoriteTvShows(sort).map { DataMapper.mapEntitiesToDomain(it) }

    override fun setMovieFavorite(movie: Movie, state: Boolean) {
        val movieEntity = DataMapper.mapDomainToEntity(movie)
        appExecutors.diskIO().execute { localDataSource.setMovieFavorite(movieEntity, state) }
    }

    private fun <T, R> createNetworkBoundResource(
        loadFromDB: () -> Flow<T>,
        shouldFetch: (T?) -> Boolean,
        createCall: suspend () -> Flow<ApiResponse<List<R>>>,
        saveCallResult: suspend (List<R>) -> Unit
    ): Flow<Resource<T>> =
        object : NetworkBoundResource<T, List<R>>() {
            override fun loadFromDB(): Flow<T> = loadFromDB()

            override fun shouldFetch(data: T?): Boolean = shouldFetch(data)

            override suspend fun createCall(): Flow<ApiResponse<List<R>>> = createCall()

            override suspend fun saveCallResult(data: List<R>) = saveCallResult(data)
        }.asFlow()
}

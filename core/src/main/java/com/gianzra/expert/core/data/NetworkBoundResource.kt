package com.gianzra.expert.core.data

import com.gianzra.expert.core.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ResultType, RequestType> {

    private val result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val dbSource = loadFromDB().first()

        if (shouldFetch(dbSource)) {
            emit(Resource.Loading())
            handleApiCall()
        } else {
            emitAll(loadFromDB().map { Resource.Success(it) })
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): Flow<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    protected abstract suspend fun saveCallResult(data: RequestType)

    fun asFlow(): Flow<Resource<ResultType>> = result

    private suspend fun FlowCollector<Resource<ResultType>>.handleApiCall() {
        val apiResponse = createCall().first()
        when (apiResponse) {
            is ApiResponse.Success -> handleSuccess(apiResponse.data)
            is ApiResponse.Empty -> emitAll(loadFromDB().map { Resource.Success(it) })
            is ApiResponse.Error -> handleError(apiResponse.errorMessage)
        }
    }

    private suspend fun FlowCollector<Resource<ResultType>>.handleSuccess(data: RequestType) {
        saveCallResult(data)
        emitAll(loadFromDB().map { Resource.Success(it) })
    }

    private suspend fun FlowCollector<Resource<ResultType>>.handleError(errorMessage: String) {
        onFetchFailed()
        emit(Resource.Error<ResultType>(errorMessage))
    }
}

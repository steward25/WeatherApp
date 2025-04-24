package com.stewardapostol.weatherapp.util
import kotlinx.coroutines.flow.*


inline fun <ResultType, RequestType> networkBoundResource(
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend () -> RequestType,
    crossinline saveFetchResult: suspend (RequestType) -> Unit,
    crossinline shouldFetch: (ResultType) -> Boolean = { true }
): Flow<Resource<ResultType>> = flow {
    // 1. Emit Loading state with the initial data (if any)
    emit(Resource.Loading(query().firstOrNull()))

    val localData = query().first() // Get the initial local data

    if (shouldFetch(localData)) {
        // 2. Fetch from network
        try {
            val fetchedResult = fetch()
            saveFetchResult(fetchedResult)
            // 3. Emit Success with the updated data from the database
            emitAll(query().map { Resource.Success(it) })
        } catch (throwable: Throwable) {
            // 4. Emit Error with the local data (if available)
            emit(Resource.Error(throwable, localData))
        }
    } else {
        // 5. Emit Success with the cached data
        emitAll(query().map { Resource.Success(it) })
    }
}
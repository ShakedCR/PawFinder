package com.pawfinder.app.data.remote.api

import retrofit2.http.GET

data class DogFactResponse(
    val data: List<DogFactData>
)

data class DogFactData(
    val attributes: DogFactAttributes
)

data class DogFactAttributes(
    val body: String
)

interface DogApiService {
    @GET("facts")
    suspend fun getFact(): DogFactResponse
}
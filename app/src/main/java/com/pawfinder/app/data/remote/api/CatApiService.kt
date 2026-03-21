package com.pawfinder.app.data.remote.api

import retrofit2.http.GET

data class CatFactResponse(
    val fact: String
)

interface CatApiService {
    @GET("fact")
    suspend fun getFact(): CatFactResponse
}
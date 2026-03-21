package com.pawfinder.app.data.repository

import com.pawfinder.app.data.remote.api.CatApiService
import com.pawfinder.app.data.remote.api.DogApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PetFactRepository {

    private val dogApi: DogApiService = Retrofit.Builder()
        .baseUrl("https://dogapi.dog/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(DogApiService::class.java)

    private val catApi: CatApiService = Retrofit.Builder()
        .baseUrl("https://catfact.ninja/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CatApiService::class.java)

    suspend fun getPetFact(petType: String): String? {
        return try {
            when (petType.lowercase()) {
                "dog" -> dogApi.getFact().data.firstOrNull()?.attributes?.body
                "cat" -> catApi.getFact().fact
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}
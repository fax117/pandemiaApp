package com.example.pandemia

import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("countries")
    suspend fun getCountries() : Response<List<PaisJson>>
    @GET("continents")
    suspend fun getContinents() : Response<List<ContinenteGson>>
}
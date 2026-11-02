package com.example.volleydemo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {

    @GET("/")
    fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") searchQuery: String,
        @Query("page") page: Int
    ): Call<OmdbSearchResponse>

    @GET("/")
    fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbId: String,
        @Query("plot") plot: String = "full"
    ): Call<OmdbDetailResponse>
}
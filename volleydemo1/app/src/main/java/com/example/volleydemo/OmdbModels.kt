package com.example.volleydemo

import com.google.gson.annotations.SerializedName

data class OmdbMovieItem(
    @SerializedName("Title") val title: String? = null,
    @SerializedName("Year") val year: String? = null,
    @SerializedName("imdbID") val imdbID: String? = null,
    @SerializedName("Type") val type: String? = null,
    @SerializedName("Poster") val poster: String? = null
)

data class OmdbSearchResponse(
    @SerializedName("Search") val search: List<OmdbMovieItem>? = null,
    @SerializedName("totalResults") val totalResults: String? = null,
    @SerializedName("Response") val response: String? = null,
    @SerializedName("Error") val error: String? = null
)

data class OmdbDetailResponse(
    @SerializedName("Title") val title: String? = null,
    @SerializedName("Year") val year: String? = null,
    @SerializedName("Rated") val rated: String? = null,
    @SerializedName("Released") val released: String? = null,
    @SerializedName("Runtime") val runtime: String? = null,
    @SerializedName("Genre") val genre: String? = null,
    @SerializedName("Director") val director: String? = null,
    @SerializedName("Writer") val writer: String? = null,
    @SerializedName("Actors") val actors: String? = null,
    @SerializedName("Plot") val plot: String? = null,
    @SerializedName("Poster") val poster: String? = null,
    @SerializedName("imdbRating") val imdbRating: String? = null,
    @SerializedName("imdbVotes") val imdbVotes: String? = null,
    @SerializedName("imdbID") val imdbID: String? = null,
    @SerializedName("Type") val type: String? = null,
    @SerializedName("Response") val response: String? = null,
    @SerializedName("Error") val error: String? = null
)
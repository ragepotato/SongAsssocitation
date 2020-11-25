package com.stephent.songasssocitation

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface JsonArtistAPI {
    @GET("api/v1/json/1/search.php?")
    fun getArtists(@Query("s") query: String) : Call<List<Artist>>
}
package com.example.fasdd_android.newsdata.retrofit

import com.example.fasdd_android.newsdata.response.ResponseNews
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("everything")
    fun getEverything(
        @Query("q") q: String,
        @Query("apiKey") apiKey: String
    ): Call<ResponseNews>
}
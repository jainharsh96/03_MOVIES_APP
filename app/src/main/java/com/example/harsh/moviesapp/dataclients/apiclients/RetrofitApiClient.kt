package com.example.harsh.moviesapp.dataclients.apiclients

import com.example.harsh.moviesapp.retrofitApiServices
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitApiClient {
    var BASE_URL = "https://api.themoviedb.org/3/"
    fun getClient() = Retrofit.Builder()
        .baseUrl(retrofitApiServices.baseuri)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}
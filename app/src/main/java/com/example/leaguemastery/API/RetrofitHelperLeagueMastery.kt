package com.example.leaguemastery.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelperLeagueMastery {

    private const val baseUrl = "http://manolodev.ddns.net:3000/"

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
object ApiClientLeagueMastery {
    val api:LeagueMasteryApi by lazy {
        RetrofitHelperLeagueMastery.retrofit.create(LeagueMasteryApi::class.java)
    }
}
package com.example.leaguemastery.API

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelperLolDataDragon {

    private const val baseUrl = "https://ddragon.leagueoflegends.com/"

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
object ApiClientLolDataDragon {
    val api:LolDataDragonApi by lazy {
        RetrofitHelperLolDataDragon.retrofit.create(LolDataDragonApi::class.java)
    }
}
package com.example.leaguemastery.API

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    private const val baseUrl = "http://manolodev.ddns.net:3000/"

    val retrofit:Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
object ApiClient {
    val api:LeagueMasteryApi by lazy {
        RetrofitHelper.retrofit.create(LeagueMasteryApi::class.java)
    }
}
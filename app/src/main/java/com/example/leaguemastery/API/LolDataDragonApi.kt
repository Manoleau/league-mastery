package com.example.leaguemastery.API

import com.example.leaguemastery.entity.*
import retrofit2.Call
import retrofit2.http.GET

interface LolDataDragonApi {
    @GET("/api/versions.json")
    fun getVersions() : Call<List<Version>>
}

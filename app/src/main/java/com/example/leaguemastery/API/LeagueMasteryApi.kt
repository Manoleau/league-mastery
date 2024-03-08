package com.example.leaguemastery.API

import com.example.leaguemastery.entity.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LeagueMasteryApi {
    @GET("/champions")
    fun getChampions() : Call<List<ChampionDefault>>
    @GET("/champions/by_name_id/{name_id}")
    fun getChampionByNameId(@Path("name_id") nameId: String): Call<ChampionDefault>
    @GET("/champions/by_name_id/{name_id}")
    fun getChampionByNameIdWithLanguage(
        @Path("name_id") nameId: String,
        @Query("language_code") languageCode: String
    ): Call<ChampionLanguage>
}

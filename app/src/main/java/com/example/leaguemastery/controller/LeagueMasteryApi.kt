package com.example.leaguemastery.controller

import com.example.leaguemastery.entity.*
import retrofit2.Response
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.Query

interface LeagueMasteryApi {
    @GET("/champions")
    suspend fun getChampions() : Response<List<ChampionDefault>>
    @GET("/champions/by_name_id/{name_id}")
    suspend fun getChampionByNameId(@Path("name_id") nameId: String): Response<ChampionDefault>
    @GET("/champions/by_name_id/{name_id}")
    suspend fun getChampionByNameIdWithLanguage(
        @Path("name_id") nameId: String,
        @Query("language_code") languageCode: String
    ): Response<ChampionLanguage>
}

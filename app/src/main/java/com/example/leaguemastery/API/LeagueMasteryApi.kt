package com.example.leaguemastery.API

import com.example.leaguemastery.entity.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
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
    @GET("/summoners/by_riot/{riot}/{tag}")
    fun getSummonerByRiotAcc(
        @Path("riot") riot: String,
        @Path("tag") tag: String
    ): Call<Summoner>
    @GET("/summoners/champions/{puuid}")
    fun getSummonerChampionLanguageByPuuid(
        @Path("puuid") puuid: String,
        @Query("language_code") languageCode: String,
        @Query("champion_id") champion_id: Int
    ): Call<ChampionSummonerLanguage>
    @GET("/summoners/champions/{puuid}")
    fun getSummonerChampionsLanguageByPuuid(
        @Path("puuid") puuid: String,
        @Query("language_code") languageCode: String,
    ): Call<List<ChampionSummonerLanguage>>
    @GET("/summoners/champions/{puuid}")
    fun getSummonerChampionDefaultByPuuid(
        @Path("puuid") puuid: String,
        @Query("champion_id") champion_id: Int
    ): Call<ChampionSummonerDefault>
    @GET("/summoners/champions/{puuid}")
    fun getSummonerChampionsDefaultByPuuid(
        @Path("puuid") puuid: String,
    ): Call<List<ChampionSummonerDefault>>
    @POST("/admin/champions/addchampionsmasteries/{puuid}")
    fun addSummonerChampions(
        @Path("puuid") puuid: String,
        @Header("x-api-key") apiKey: String
    ): Call<List<ChampionSummonerDefault>>
    @PUT("/admin/summoners/{puuid}")
    fun updateSummoner(
        @Path("puuid") puuid: String,
        @Header("x-api-key") apiKey: String
    ): Call<Summoner>

    @PUT("/admin/summoners/{puuid}")
    fun updateSummonerChampions(
        @Path("puuid") puuid: String,
        @Header("x-api-key") apiKey: String
    ): Call<List<ChampionSummonerAbstract>>

}

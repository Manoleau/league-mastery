package com.example.leaguemastery.entity

data class Summoner(
    val summonerId: String,
    val accountId: String,
    val puuid: String,
    val server: String,
    val riotName: String,
    val tag: String,
    val profileIconId: Int,
    val summonerLevel: Int,
    var masteryPoints: Int = 0
)

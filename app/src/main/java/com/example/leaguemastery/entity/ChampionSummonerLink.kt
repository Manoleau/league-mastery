package com.example.leaguemastery.entity

data class ChampionSummonerLink(
    val championLevel: Int,
    val championPoints: Int,
    val championPointsSinceLastLevel: Int,
    val championPointsUntilNextLevel: Int,
    val chestGranted: Boolean,
    val summoner: Summoner,
    val champion: Championtmp
)

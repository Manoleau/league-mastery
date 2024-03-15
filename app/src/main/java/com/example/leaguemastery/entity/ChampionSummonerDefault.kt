package com.example.leaguemastery.entity

class ChampionSummonerDefault(
    championLevel: Int?,
    championPoints: Int?,
    championPointsSinceLastLevel: Int?,
    championPointsUntilNextLevel: Int?,
    chestGranted: Boolean?,
    summoner: Summoner?,
    val champion:ChampionDefault
) :
    ChampionSummonerAbstract(
        championLevel,championPoints,championPointsSinceLastLevel,championPointsUntilNextLevel,chestGranted, summoner
    )

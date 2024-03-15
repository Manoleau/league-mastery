package com.example.leaguemastery.entity

class ChampionSummonerLanguage(
    championLevel: Int?,
    championPoints: Int?,
    championPointsSinceLastLevel: Int?,
    championPointsUntilNextLevel: Int?,
    chestGranted: Boolean?,
    summoner: Summoner?,
    val champion:ChampionLanguage
) :
    ChampionSummonerAbstract(
        championLevel,championPoints,championPointsSinceLastLevel,championPointsUntilNextLevel,chestGranted, summoner
    )

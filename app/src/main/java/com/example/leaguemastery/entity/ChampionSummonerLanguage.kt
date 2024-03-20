package com.example.leaguemastery.entity

class ChampionSummonerLanguage(
    championLevel: Int?,
    championPoints: Int?,
    championPointsSinceLastLevel: Int?,
    championPointsUntilNextLevel: Int?,
    chestGranted: Boolean?,
    val champion:ChampionLanguage,
    val summoner: Summoner?

) :
    ChampionSummonerAbstract(
        championLevel,championPoints,championPointsSinceLastLevel,championPointsUntilNextLevel,chestGranted
    )

package com.example.leaguemastery.entity

class ChampionSummonerDefault(
    championLevel: Int?,
    championPoints: Int?,
    championPointsSinceLastLevel: Int?,
    championPointsUntilNextLevel: Int?,
    chestGranted: Boolean?,
    val champion:ChampionDefault,
    val summoner: Summoner?
) :
    ChampionSummonerAbstract(
        championLevel,championPoints,championPointsSinceLastLevel,championPointsUntilNextLevel,chestGranted
    )

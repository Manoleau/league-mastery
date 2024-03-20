package com.example.leaguemastery.entity

abstract class ChampionSummonerAbstract(
    val championLevel: Int?,
    val championPoints: Int?,
    val championPointsSinceLastLevel: Int?,
    val championPointsUntilNextLevel: Int?,
    val chestGranted: Boolean?,
)

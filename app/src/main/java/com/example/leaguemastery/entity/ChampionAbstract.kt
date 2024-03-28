package com.example.leaguemastery.entity

abstract class ChampionAbstract(
    private val _id: String?,
    val key: Int?,
    val nameId: String?,
    val imageIcon: String?,
    val imageSplash: String?,
    val imageLoadScreen: String?,
    val roleDefaults: List<RoleDefault?>?
)

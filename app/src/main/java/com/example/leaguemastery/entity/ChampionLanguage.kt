package com.example.leaguemastery.entity

class ChampionLanguage(
    _id: String?,
    key: Int?,
    name_id: String?,
    image_icon: String?,
    image_splash: String?,
    image_load_screen: String?,
    roles: List<Role?>?,
    val name: String,
    val language_code: String,
    val title: String
) :
    ChampionAbstract(
        _id!!, key!!,
        name_id!!, image_icon!!, image_splash!!, image_load_screen!!, roles
    )

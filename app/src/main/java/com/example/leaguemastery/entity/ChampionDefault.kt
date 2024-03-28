package com.example.leaguemastery.entity

class ChampionDefault(
    _id: String?,
    key: Int?,
    nameId: String?,
    imageIcon: String?,
    imageSplash: String?,
    imageLoadScreen: String?,
    roleDefaults: List<RoleDefault?>?,
    val defaultName: String,
    val defaultTitle: String
) :
    ChampionAbstract(
        _id, key, nameId, imageIcon, imageSplash, imageLoadScreen, roleDefaults
    )

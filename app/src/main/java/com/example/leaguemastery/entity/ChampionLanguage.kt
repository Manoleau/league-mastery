package com.example.leaguemastery.entity

class ChampionLanguage(
    _id: String?,
    key: Int?,
    nameId: String?,
    imageIcon: String?,
    imageSplash: String?,
    imageLoadScreen: String?,
    roleDefaults: List<RoleDefault?>?,
    val name: String,
    val languageCode: String,
    val title: String
) :
    ChampionAbstract(
        _id, key, nameId, imageIcon, imageSplash, imageLoadScreen, roleDefaults
    )

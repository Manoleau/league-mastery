package com.example.leaguemastery.entity

class RoleLanguage(
    _id:String,
    imageIcon: String,
    val translateName: String,
    val languageCode: String
) :
    Role(
        _id, imageIcon
    )
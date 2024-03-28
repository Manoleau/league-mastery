package com.example.leaguemastery.entity

class RoleLanguage(
    _id:String,
    image_icon: String,
    val translate_name: String,
    val language_code: String
) :
    Role(
        _id, image_icon
    )
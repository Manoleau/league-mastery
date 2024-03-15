package com.example.leaguemastery.entity

abstract class ChampionAbstract(
    private val _id: String?,
    val key: Int?,
    val name_id: String?,
    val image_icon: String?,
    val image_splash: String?,
    val image_load_screen: String?,
    val roles: List<Role?>?
) {

    fun get_id(): String? {
        return _id
    }
}

package com.example.leaguemastery.entity

class Championtmp {
    var _id: String
        private set
    var name: String
        private set
    var title: String
        private set
    var language_code: String?
        private set
    var key: Int
        private set
    var name_id: String
        private set
    var image_icon: String
        private set
    var image_splash: String
        private set
    var image_load_screen: String
        private set
    var roles: List<Role>
        private set

    constructor(
        _id: String,
        name: String,
        title: String,
        language_code: String?,
        key: Int,
        name_id: String,
        image_icon: String,
        image_splash: String,
        image_load_screen: String
    ) {
        this._id = _id
        this.name = name
        this.title = title
        this.language_code = language_code
        this.key = key
        this.name_id = name_id
        this.image_icon = image_icon
        this.image_splash = image_splash
        this.image_load_screen = image_load_screen
        roles = ArrayList()
    }

    constructor(
        _id: String,
        name: String,
        title: String,
        key: Int,
        name_id: String,
        image_icon: String,
        image_splash: String,
        image_load_screen: String
    ) {
        this._id = _id
        this.name = name
        this.title = title
        language_code = null
        this.key = key
        this.name_id = name_id
        this.image_icon = image_icon
        this.image_splash = image_splash
        this.image_load_screen = image_load_screen
        roles = ArrayList()
    }


}

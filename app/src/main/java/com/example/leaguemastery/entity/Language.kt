package com.example.leaguemastery.entity

enum class Language(val displayName: String, val code: String) {
    EN_US("English (United States)", "en_US"),
    CS_CZ("Czech (Czech Republic)", "cs_CZ"),
    DE_DE("German (Germany)", "de_DE"),
    EL_GR("Greek (Greece)", "el_GR"),
    EN_AU("English (Australia)", "en_AU"),
    EN_GB("English (United Kingdom)", "en_GB"),
    EN_PH("English (Republic of the Philippines)", "en_PH"),
    EN_SG("English (Singapore)", "en_SG"),
    ES_AR("Spanish (Argentina)", "es_AR"),
    ES_ES("Spanish (Spain)", "es_ES"),
    ES_MX("Spanish (Mexico)", "es_MX"),
    FR_FR("French (France)", "fr_FR"),
    HU_HU("Hungarian (Hungary)", "hu_HU"),
    IT_IT("Italian (Italy)", "it_IT"),
    JA_JP("Japanese (Japan)", "ja_JP"),
    KO_KR("Korean (Korea)", "ko_KR"),
    PL_PL("Polish (Poland)", "pl_PL"),
    PT_BR("Portuguese (Brazil)", "pt_BR"),
    RO_RO("Romanian (Romania)", "ro_RO"),
    RU_RU("Russian (Russia)", "ru_RU"),
    TH_TH("Thai (Thailand)", "th_TH"),
    TR_TR("Turkish (Turkey)", "tr_TR"),
    VI_VN("Vietnamese (Viet Nam)", "vi_VN"),
    ZH_CN("Chinese (China)", "zh_CN"),
    ZH_MY("Chinese (Malaysia)", "zh_MY"),
    ZH_TW("Chinese (Taiwan)", "zh_TW");

    companion object {
        fun fromCode(code: String): Language? = values().find { it.code == code }
    }
}

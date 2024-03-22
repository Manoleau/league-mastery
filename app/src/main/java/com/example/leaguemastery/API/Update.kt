package com.example.leaguemastery.API

import android.R
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import com.example.leaguemastery.entity.ChampionSummonerAbstract
import com.example.leaguemastery.entity.Summoner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Update {
    companion object{
        val listViewUpdate: ArrayList<View> = ArrayList()
        fun updateSummoner(puuid:String){
            listViewUpdate.forEach { it.isEnabled = false }
            ApiClientLeagueMastery.api.updateSummoner(puuid, "GGEZ").enqueue(object : Callback<Summoner>{
                override fun onResponse(
                    call: Call<Summoner>,
                    response: Response<Summoner>
                ) {
                    ApiClientLeagueMastery.api.updateSummonerChampions(puuid, "GGEZ").enqueue(object : Callback<List<ChampionSummonerAbstract>>{
                        override fun onResponse(
                            call: Call<List<ChampionSummonerAbstract>>,
                            response: Response<List<ChampionSummonerAbstract>>
                        ) {
                            listViewUpdate.forEach { it.isEnabled = true }
                        }

                        override fun onFailure(call: Call<List<ChampionSummonerAbstract>>, t: Throwable) {
                            listViewUpdate.forEach { it.isEnabled = true }
                        }
                    })
                }

                override fun onFailure(call: Call<Summoner>, t: Throwable) {
                    listViewUpdate.forEach { it.isEnabled = true }
                }
            })
        }
    }
}
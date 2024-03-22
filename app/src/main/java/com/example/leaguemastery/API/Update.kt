package com.example.leaguemastery.API

import android.R
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leaguemastery.Cache
import com.example.leaguemastery.entity.ChampionSummonerAbstract
import com.example.leaguemastery.entity.ChampionSummonerDefault
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.example.leaguemastery.entity.Summoner
import com.example.leaguemastery.ui.profile.MasteryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Update {
    companion object{
        val listViewUpdate: ArrayList<View> = ArrayList()
        var isBackButtonDisabled = false

        fun updateSummoner(puuid:String, context: Context, progressBar: ProgressBar){
            listViewUpdate.forEach { it.isEnabled = false }
            progressBar.visibility = View.VISIBLE
            isBackButtonDisabled = true
            Cache.adapterM?.loadMastery(ArrayList())
            ApiClientLeagueMastery.api.updateSummoner(puuid, "GGEZ").enqueue(object : Callback<Summoner>{
                override fun onResponse(
                    call: Call<Summoner>,
                    response: Response<Summoner>
                ) {
                    ApiClientLeagueMastery.api.updateSummonerChampions(puuid, "GGEZ").enqueue(object : Callback<List<ChampionSummonerDefault>>{
                        override fun onResponse(
                            call: Call<List<ChampionSummonerDefault>>,
                            response: Response<List<ChampionSummonerDefault>>
                        ) {
                            listViewUpdate.forEach { it.isEnabled = true }
                            isBackButtonDisabled = false
                            progressBar.visibility = View.GONE
                            setChampionMastery(context)
                        }

                        override fun onFailure(call: Call<List<ChampionSummonerDefault>>, t: Throwable) {
                            listViewUpdate.forEach { it.isEnabled = true }
                            isBackButtonDisabled = false
                            progressBar.visibility = View.GONE
                        }
                    })
                }

                override fun onFailure(call: Call<Summoner>, t: Throwable) {
                    listViewUpdate.forEach { it.isEnabled = true }
                    isBackButtonDisabled = false
                    progressBar.visibility = View.GONE
                }
            })
        }

        fun setChampionMastery(context: Context){
            val callMasteryChampion = ApiClientLeagueMastery.api.getSummonerChampionsLanguageByPuuid(Cache.actualSummoner?.puuid!!, Cache.langue.code)
            callMasteryChampion.enqueue(object : Callback<List<ChampionSummonerLanguage>>{
                override fun onResponse(call: Call<List<ChampionSummonerLanguage>>, response: Response<List<ChampionSummonerLanguage>>) {
                    if(response.isSuccessful){
                        val masteryList = response.body()

                        if(masteryList != null){
                            val sortedMasteryList = masteryList.sortedByDescending { it.championPoints ?: 0 }
                            Cache.actualSummonerChampion = sortedMasteryList
                            Cache.adapterM?.loadMastery(sortedMasteryList)
                            Toast.makeText(context, "Mise à jour finie", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Erreur ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<List<ChampionSummonerLanguage>>, t: Throwable) {
                    Toast.makeText(context, "Erreur ${t.message}", Toast.LENGTH_SHORT).show()

                }
            })
        }
    }
}
package com.example.leaguemastery.API

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leaguemastery.Cache
import com.example.leaguemastery.entity.ChampionSummonerDefault
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.example.leaguemastery.entity.Summoner
import com.example.leaguemastery.ui.profile.MasteryAdapter
import com.example.leaguemastery.ui.profile.ProfileFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Update {
    companion object{
        val listViewUpdate: ArrayList<View> = ArrayList()
        var isBackButtonDisabled = false
        fun updateSummoner(puuid:String, context: Context, progressBar: ProgressBar){
            if(!Cache.updating){
                progressBar.visibility = View.VISIBLE

                Cache.updating = true;
                isBackButtonDisabled = true
                listViewUpdate.forEach { it.isEnabled = false }
                Cache.adapterM?.loadMastery(ArrayList())
                Cache.actualSummonerChampion = ArrayList()
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
                                Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
                                listViewUpdate.forEach { it.isEnabled = true }
                                isBackButtonDisabled = false
                                progressBar.visibility = View.GONE
                            }
                        })
                    }

                    override fun onFailure(call: Call<Summoner>, t: Throwable) {
                        Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
                        listViewUpdate.forEach { it.isEnabled = true }
                        isBackButtonDisabled = false
                        progressBar.visibility = View.GONE
                    }
                })
            }
            else{
                Toast.makeText(context, "Une mise à jour est en cours", Toast.LENGTH_SHORT).show()
            }
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
                            Cache.adapterM = MasteryAdapter(Cache.actualSummonerChampion)
                            ProfileFragment.masteryRecyclerView?.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = Cache.adapterM
                            }
                            ProfileFragment.setProfileInViews(context)
                            Toast.makeText(context, "Mise à jour finie", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Erreur ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                    Cache.updating = false
                }
                override fun onFailure(call: Call<List<ChampionSummonerLanguage>>, t: Throwable) {
                    Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
                    Cache.updating = false
                }
            })
        }
    }
}
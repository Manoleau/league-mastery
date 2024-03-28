package com.example.leaguemastery.API

import android.app.ProgressDialog
import android.content.Context
import android.view.View
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


/**
 * Classe de gestion des mises à jour pour l'application League Mastery.
 * Permet de mettre à jour les informations d'un invocateur et de ses champions depuis une API.
 */
class Update {
    companion object{
        val listViewUpdate: ArrayList<View> = ArrayList()
        var isBackButtonDisabled = false
        lateinit var loadingBar: ProgressDialog
        var updating: Boolean = false

        /**
         * Lance la mise à jour des informations de l'invocateur spécifié.
         *
         * @param puuid Identifiant unique de l'invocateur à mettre à jour.
         * @param context Contexte de l'application pour afficher des messages et des dialogues.
         */
        fun updateSummoner(puuid:String, context: Context){
            if(!updating){
                loadingBar = ProgressDialog(context)
                loadingBar.setTitle("Mise à jour en cours...")
                loadingBar.setMessage("Veillez patienter")
                loadingBar.setCancelable(false)
                loadingBar.show()
                updating = true
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
                                loadingBar.dismiss()
                                ProfileFragment.setProfileInViews(context)
                                setChampionMastery(context)
                            }

                            override fun onFailure(call: Call<List<ChampionSummonerDefault>>, t: Throwable) {
                                loadingBar.dismiss()
                                Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
                                listViewUpdate.forEach { it.isEnabled = true }
                                isBackButtonDisabled = false
                                updating = false
                            }
                        })
                    }

                    override fun onFailure(call: Call<Summoner>, t: Throwable) {
                        loadingBar.dismiss()
                        Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
                        listViewUpdate.forEach { it.isEnabled = true }
                        isBackButtonDisabled = false
                        updating = false
                    }
                })
            }
            else{
                Toast.makeText(context, "Une mise à jour est en cours", Toast.LENGTH_SHORT).show()
            }
        }

        /**
         * Met à jour la maîtrise des champions de l'invocateur actuel en utilisant les données de l'API.
         *
         * @param context Contexte de l'application.
         */
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
                            ProfileFragment.dbHelper!!.updateSummoner("${Cache.actualSummoner?.riotName}#${Cache.actualSummoner?.tag}", Cache.actualSummoner!!.puuid, Cache.actualSummoner!!.profileIconId)

                            Toast.makeText(context, "Mise à jour finie", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Erreur ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                    updating = false
                }
                override fun onFailure(call: Call<List<ChampionSummonerLanguage>>, t: Throwable) {
                    Toast.makeText(context, "Une erreur s'est produite", Toast.LENGTH_SHORT).show()
                    updating = false
                }
            })
        }
    }
}
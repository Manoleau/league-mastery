package com.example.leaguemastery

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import com.example.leaguemastery.API.ApiClient
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.entity.ChampionSummonerDefault
import com.example.leaguemastery.entity.Summoner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Acceuil : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceuil)
        val btn_search = findViewById<Button>(R.id.search_button)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        dbHelper = DBHelper(this, null)
        val context = this
        val riotAccText = findViewById<AutoCompleteTextView>(R.id.search_field)
        val themeToggleSwitch: SwitchCompat = findViewById(R.id.themeToggleSwitch)

        setAutoCompleteRiotAcc(riotAccText)
        btn_search.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val tmp = riotAccText.text.split("#")

            if(tmp.size == 2){
                val callSummoner = ApiClient.api.getSummonerByRiotAcc(tmp[0], tmp[1])
                callSummoner.enqueue(object :Callback<Summoner>{
                    override fun onResponse(call: Call<Summoner>, response: Response<Summoner>) {
                        if(response.isSuccessful){
                            val summoner = response.body()
                            if(summoner != null){
                                dbHelper.addSummoner(riotAccText.text.toString())
                                //setAutoCompleteRiotAcc(riotAccText)
                                val callSummonerChampion = ApiClient.api.addSummonerChampions(summoner.puuid, "GGEZ")
                                callSummonerChampion.enqueue(object : Callback<List<ChampionSummonerDefault>>{
                                    override fun onResponse(
                                        call: Call<List<ChampionSummonerDefault>>,
                                        response: Response<List<ChampionSummonerDefault>>
                                    ) {
                                        progressBar.visibility = View.GONE
                                        val intent = Intent(context, MainActivity::class.java).apply {
                                            putExtra("summonerId", summoner.summonerId)
                                            putExtra("accountId", summoner.accountId)
                                            putExtra("puuid", summoner.puuid)
                                            putExtra("server", summoner.server)
                                            putExtra("summonerName", summoner.summonerName)
                                            putExtra("riotName", summoner.riotName)
                                            putExtra("tag", summoner.tag)
                                            putExtra("profileIconId", summoner.profileIconId)
                                            putExtra("summonerLevel", summoner.summonerLevel)
                                        }
                                        startActivity(intent)
                                    }
                                    override fun onFailure(
                                        call: Call<List<ChampionSummonerDefault>>,
                                        t: Throwable
                                    ) {
                                        Toast.makeText(context, "Erreur de chargement : ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        } else {
                            Toast.makeText(context, "Joueur introuvable", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                    }
                    override fun onFailure(call: Call<Summoner>, t: Throwable) {
                        t.message
                        Toast.makeText(context, "Erreur de chargement : ${t.message}", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE

                    }
                })
            } else {
                Toast.makeText(context, "Joueur introuvable", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }

        // Initialisez l'état du switch selon le mode nuit actuel
        themeToggleSwitch.isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        themeToggleSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val riotAccText = findViewById<AutoCompleteTextView>(R.id.search_field)
        setAutoCompleteRiotAcc(riotAccText)

    }
    fun setAutoCompleteRiotAcc(autoCompleteTextView:AutoCompleteTextView){
        val summonersDB = ArrayList<String>()
        for (summ in dbHelper.getAllSummoner()){
            summonersDB.add(summ.riotacc)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,summonersDB)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }
}
package com.example.leaguemastery

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import com.example.leaguemastery.API.ApiClientLeagueMastery
import com.example.leaguemastery.API.ApiClientLolDataDragon
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.entity.ChampionSummonerDefault
import com.example.leaguemastery.entity.Language
import com.example.leaguemastery.entity.RoleLanguage
import com.example.leaguemastery.entity.Summoner
import com.example.leaguemastery.ui.profile.MasteryAdapter
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Acceuil : AppCompatActivity() {
    private lateinit var dbHelper: DBHelper
    lateinit var firebaseAuth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceuil)
        val btn_search = findViewById<Button>(R.id.search_button)
        val progressBar = findViewById<ProgressBar>(R.id.progressBarAcceuil)
        dbHelper = DBHelper(this, null)
        val context = this
        val riotAccText = findViewById<AutoCompleteTextView>(R.id.search_field)
        if(Cache.version == ""){
            val callVersion = ApiClientLolDataDragon.api.getVersions()
            callVersion.enqueue(object : Callback<List<String>>{
                override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                    if(response.isSuccessful){
                        val versions = response.body()
                        Cache.version = versions!![0]
                        Cache.downloadAndSetImages(context, dbHelper)
                    } else {
                        Cache.version = "14.5.1"
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Cache.version = "14.5.1"
                }
            })
        }
        val languages = Language.entries.toTypedArray()
        val languagesStr = ArrayList<String>()
        for (language in languages){
            languagesStr.add(language.displayName)
        }
        val spinner: Spinner = findViewById(R.id.language_selector)
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languagesStr)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                Cache.langue = languages[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Cache.langue = Language.FR_FR
            }
        }

        setAutoCompleteRiotAcc(riotAccText)

        firebaseAuth = FirebaseAuth.getInstance()
        btn_search.setOnClickListener {
            btn_search.isEnabled = false
            Cache.actualSummonerChampion = ArrayList()
            Cache.adapterM = MasteryAdapter(ArrayList(), dbHelper)
            Cache.actualSummoner = null
            progressBar.visibility = View.VISIBLE
            val riotAccInText = riotAccText.text
            val tmp = riotAccInText.split("#")

            if(tmp.size == 2){
                val summoner = dbHelper.getSummoner(riotAccInText.toString())
                val callSummoner:Call<Summoner>
                if(summoner != null){
                    callSummoner = ApiClientLeagueMastery.api.getSummonerByPuuid(summoner.puuid)
                } else {
                    callSummoner = ApiClientLeagueMastery.api.getSummonerByRiotAcc(tmp[0], tmp[1])
                }
                callSummoner.enqueue(object :Callback<Summoner>{
                    override fun onResponse(call: Call<Summoner>, response: Response<Summoner>) {
                        if(response.isSuccessful){
                            val summoner = response.body()
                            if(summoner != null){
                                dbHelper.addSummoner(riotAccText.text.toString(),summoner.puuid)
                                val callSummonerChampion = ApiClientLeagueMastery.api.addSummonerChampions(summoner.puuid, "GGEZ")
                                callSummonerChampion.enqueue(object : Callback<List<ChampionSummonerDefault>>{
                                    override fun onResponse(
                                        call: Call<List<ChampionSummonerDefault>>,
                                        response: Response<List<ChampionSummonerDefault>>
                                    ) {
                                        progressBar.visibility = View.GONE
                                        Cache.actualSummoner = summoner
                                        startActivity(Intent(context, MainActivity::class.java))

                                        btn_search.isEnabled = true
                                    }
                                    override fun onFailure(
                                        call: Call<List<ChampionSummonerDefault>>,
                                        t: Throwable
                                    ) {
                                        btn_search.isEnabled = true
                                        Toast.makeText(context, "Aucune connexion internet", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        } else {
                            Toast.makeText(context, "Joueur introuvable", Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                    }
                    override fun onFailure(call: Call<Summoner>, t: Throwable) {
                        btn_search.isEnabled = true
                        Toast.makeText(context, "Aucune connexion internet", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE

                    }
                })
            } else {
                btn_search.isEnabled = true
                Toast.makeText(context, "Joueur introuvable", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }


    fun setRoles(context: Context, language: Language){
        ApiClientLeagueMastery.api.getRolesLanguage(language.code).enqueue(object : Callback<List<RoleLanguage>>{
            override fun onResponse(
                call: Call<List<RoleLanguage>>,
                response: Response<List<RoleLanguage>>
            ) {

            }

            override fun onFailure(call: Call<List<RoleLanguage>>, t: Throwable) {
                Toast.makeText(context, "Aucune connexion internet", Toast.LENGTH_SHORT).show()
            }
        })
    }
    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        val riotAccText = findViewById<AutoCompleteTextView>(R.id.search_field)
        setAutoCompleteRiotAcc(riotAccText)

    }
    override fun onDestroy() {
        super.onDestroy()
        //Cache.saveInPhone(dbHelper)
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
package com.example.leaguemastery

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leaguemastery.API.ApiClientLeagueMastery
import com.example.leaguemastery.API.ApiClientLolDataDragon
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.entity.ChampionSummonerDefault
import com.example.leaguemastery.entity.Language
import com.example.leaguemastery.entity.Summoner
import com.example.leaguemastery.ui.profile.MasteryAdapter
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AcceuilRecherche : AppCompatActivity() {
    private lateinit var firebaseAuth:FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceuil_recherche)
        btn_search = findViewById(R.id.search_button)
        val context = this
        riotAccText = findViewById(R.id.search_field)
        val spinner: Spinner = findViewById(R.id.language_selector)
        loadingBar = ProgressDialog(this)
        dbHelper = DBHelper(this, null)
        if(Cache.isOnline(this@AcceuilRecherche)){
            firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser
            var uid = "0"
            if(currentUser != null){
                uid = currentUser.uid
            }
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
            spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, languagesStr)
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    Cache.langue = languages[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    Cache.langue = Language.FR_FR
                }
            }
            val favoris = dbHelper.getFavs(uid, this@AcceuilRecherche)
            findViewById<RecyclerView>(R.id.favorisRecyclerView).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = FavorisAdapter(favoris)
            }

            setAutoCompleteRiotAcc(riotAccText)

            btn_search.setOnClickListener {
                searchPlayer(this@AcceuilRecherche)
            }
        }
        else{
            btn_search.isEnabled = false
            riotAccText.isEnabled = false
            spinner.isEnabled = false
        }
    }
    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if(Cache.isOnline(this@AcceuilRecherche)){
            riotAccText = findViewById(R.id.search_field)
            setAutoCompleteRiotAcc(riotAccText)
            firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser
            var uid = "0"
            if(currentUser != null){
                uid = currentUser.uid
            }
            val favoris = dbHelper.getFavs(uid, this@AcceuilRecherche)
            findViewById<RecyclerView>(R.id.favorisRecyclerView).apply {
                layoutManager = LinearLayoutManager(context)
                adapter = FavorisAdapter(favoris)
            }
        }

    }
    private fun setAutoCompleteRiotAcc(autoCompleteTextView:AutoCompleteTextView){
        val summonersDB = ArrayList<String>()
        for (summ in dbHelper.getAllSummoner()){
            summonersDB.add(summ.riotacc)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line,summonersDB)
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 1
    }

    companion object{
        private lateinit var dbHelper: DBHelper
        lateinit var btn_search: Button
        lateinit var riotAccText: AutoCompleteTextView
        private lateinit var loadingBar: ProgressDialog
        fun searchPlayer(context: Context){

            btn_search.isEnabled = false
            Cache.actualSummonerChampion = ArrayList()
            Cache.adapterM = MasteryAdapter(ArrayList())
            Cache.actualSummoner = null
            loadingBar.setTitle("Recherche en cours...")
            loadingBar.setMessage("Veillez patienter")
            loadingBar.show()
            val riotAccInText = riotAccText.text
            val tmp = riotAccInText.split("#")

            if(tmp.size == 2){
                val summoner = dbHelper.getSummoner(riotAccInText.toString())
                val callSummoner:Call<Summoner> = if(summoner != null){
                    ApiClientLeagueMastery.api.getSummonerByPuuid(summoner.puuid)
                } else {
                    ApiClientLeagueMastery.api.getSummonerByRiotAcc(tmp[0], tmp[1])
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
                                        loadingBar.dismiss()
                                        Cache.actualSummoner = summoner
                                        context.startActivity(Intent(context, MainActivity::class.java))
                                        btn_search.isEnabled = true
                                    }
                                    override fun onFailure(
                                        call: Call<List<ChampionSummonerDefault>>,
                                        t: Throwable
                                    ) {
                                        loadingBar.dismiss()
                                        btn_search.isEnabled = true
                                        Toast.makeText(context, "Essayez avec une autre connexion", Toast.LENGTH_SHORT).show()
                                    }
                                })
                            }
                        } else {
                            loadingBar.dismiss()
                            Toast.makeText(context, "Joueur introuvable", Toast.LENGTH_SHORT).show()
                            btn_search.isEnabled = true
                        }
                    }
                    override fun onFailure(call: Call<Summoner>, t: Throwable) {
                        btn_search.isEnabled = true
                        loadingBar.dismiss()
                        Toast.makeText(context, "Essayez avec une autre connexion", Toast.LENGTH_SHORT).show()

                    }
                })
            } else {
                btn_search.isEnabled = true
                loadingBar.dismiss()
                Toast.makeText(context, "Joueur introuvable", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
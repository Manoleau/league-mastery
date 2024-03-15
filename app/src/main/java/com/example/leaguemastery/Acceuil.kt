package com.example.leaguemastery

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.leaguemastery.API.ApiClient
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.entity.Summoner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Acceuil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acceuil)
        val btn_search = findViewById<Button>(R.id.search_button)
        val txt_rep = findViewById<TextView>(R.id.content_text)
        val db = DBHelper(this, null)
        btn_search.setOnClickListener {
            val riotAccText = findViewById<EditText>(R.id.search_field)
            val tmp = riotAccText.text.split("#")

            if(tmp.size == 2){
                val call = ApiClient.api.getSummonerByRiotAcc(tmp[0], tmp[1])
                call.enqueue(object :Callback<Summoner>{
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(call: Call<Summoner>, response: Response<Summoner>) {
                        if(response.isSuccessful){
                            val summoner = response.body()
                            if(summoner != null){
                                db.addSummoner(riotAccText.text.toString())
                                txt_rep.text = "Nom : ${summoner.summonerName}\nNiveau : ${summoner.summonerLevel}"
                            }
                        } else {
                            txt_rep.text = "Joueur introuvable"
                        }
                    }
                    @SuppressLint("SetTextI18n")
                    override fun onFailure(call: Call<Summoner>, t: Throwable) {
                        txt_rep.text = "Erreur $t"
                    }
                })
            } else {
                txt_rep.text = "Joueur introuvable"
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }
}
package com.example.leaguemastery.ui.profile

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leaguemastery.API.ApiClientLeagueMastery
import com.example.leaguemastery.Cache
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.databinding.FragmentProfileBinding
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var dbHelper: DBHelper

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val progressBar = binding.progressBarMain
        progressBar.visibility = View.VISIBLE
        val root: View = binding.root
        val activity = requireActivity()
        val intent = activity.intent
        val context = root.context
        dbHelper = DBHelper(context, null)

        val summonerId = intent.getStringExtra("summonerId")
        val accountId = intent.getStringExtra("accountId")
        val puuid = intent.getStringExtra("puuid")
        val server = intent.getStringExtra("server")

        val summonerName = intent.getStringExtra("summonerName")
        val riotName = intent.getStringExtra("riotName")
        val tag = intent.getStringExtra("tag")
        val profileIconId = intent.getIntExtra("profileIconId",-1)
        val summonerLevel = intent.getIntExtra("summonerLevel", 0)
        val profileIconImageView:ImageView = binding.profileIconImageView
        val riotNameTextView:TextView = binding.riotNameTextView
        val summonerLevelTextView:TextView = binding.summonerLevelTextView
        val handler = Handler(Looper.getMainLooper())
        var iconSummoner:Drawable?
        if(Cache.data[profileIconId.toString()] == null){
            Thread{
                iconSummoner = Cache.setImage(
                    "https://ddragon.leagueoflegends.com/cdn/${Cache.version}/img/profileicon/${profileIconId}.png",
                    profileIconId.toString(),
                    "image",
                    Cache.version,
                    dbHelper,
                    context
                )
                handler.post{
                    if (riotName != null && tag != null && iconSummoner != null) {
                        setProfile(profileIconImageView, riotNameTextView, summonerLevelTextView, riotName, tag, summonerLevel, iconSummoner!!)
                    }
                }
            }.start()
        } else {
            iconSummoner = Cache.data[profileIconId.toString()]?.get("image")
            if (riotName != null && tag != null && iconSummoner != null) {
                setProfile(profileIconImageView, riotNameTextView, summonerLevelTextView, riotName, tag, summonerLevel, iconSummoner!!)
            }
        }
        val callMasteryChampion = ApiClientLeagueMastery.api.getSummonerChampionsLanguageByPuuid(puuid!!, Cache.langue.code)
        callMasteryChampion.enqueue(object : Callback<List<ChampionSummonerLanguage>>{
            override fun onResponse(call: Call<List<ChampionSummonerLanguage>>, response: Response<List<ChampionSummonerLanguage>>) {
                if(response.isSuccessful){
                    val masteryList = response.body()

                    if(masteryList != null){
                        val sortedMasteryList = masteryList.sortedByDescending { it.championPoints ?: 0 }
                        val adapterM = MasteryAdapter(sortedMasteryList, dbHelper)
                        val searchEditText = binding.searchChampionEditText

                        binding.masteryRecyclerView.apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = adapterM
                        }
                    }
                } else {
                    Toast.makeText(context, "Erreur ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<ChampionSummonerLanguage>>, t: Throwable) {
                Toast.makeText(context, "Erreur ${t.message}", Toast.LENGTH_SHORT).show()

            }
        })
        progressBar.visibility = View.GONE
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setProfile(profileIconImageView:ImageView, riotNameTextView:TextView, summonerLevelTextView:TextView, riotName:String, tag:String, summonerLevel:Int, iconSummoner:Drawable){
        profileIconImageView.setImageDrawable(iconSummoner)
        riotNameTextView.text = "$riotName#$tag"
        summonerLevelTextView.text = "Level: $summonerLevel"
    }

}
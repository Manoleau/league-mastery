package com.example.leaguemastery.ui.profile

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leaguemastery.API.ApiClientLeagueMastery
import com.example.leaguemastery.Cache
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.R
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
        val root: View = binding.root
        val context = root.context
        dbHelper = DBHelper(context, null)
        val championFiltre = binding.searchChampionEditText
        val profileIconImageView:ImageView = binding.profileIconImageView
        val riotNameTextView:TextView = binding.riotNameTextView
        val summonerLevelTextView:TextView = binding.summonerLevelTextView
        val handler = Handler(Looper.getMainLooper())
        var iconSummoner:Drawable?
        val summoner = Cache.actualSummoner
        if(Cache.data[summoner?.profileIconId.toString()] == null){
            Thread{
                iconSummoner = Cache.setImage(
                    "https://ddragon.leagueoflegends.com/cdn/${Cache.version}/img/profileicon/${summoner?.profileIconId}.png",
                    summoner?.profileIconId.toString(),
                    "image",
                    Cache.version,
                    dbHelper,
                    context
                )
                handler.post{
                    if (summoner?.riotName != null && tag != null && iconSummoner != null) {
                        setProfile(profileIconImageView, riotNameTextView, summonerLevelTextView, summoner.riotName, summoner.tag, summoner.summonerLevel, iconSummoner!!)
                    }
                }
            }.start()
        }
        else {
            iconSummoner = Cache.data[summoner?.profileIconId.toString()]?.get("image")
            if (summoner?.riotName != null && tag != null && iconSummoner != null) {
                setProfile(profileIconImageView, riotNameTextView, summonerLevelTextView, summoner.riotName, summoner.tag, summoner.summonerLevel, iconSummoner!!)
            }
        }
        if(Cache.actualSummonerChampion.isEmpty()){
            val callMasteryChampion = ApiClientLeagueMastery.api.getSummonerChampionsLanguageByPuuid(summoner?.puuid!!, Cache.langue.code)
            callMasteryChampion.enqueue(object : Callback<List<ChampionSummonerLanguage>>{
                override fun onResponse(call: Call<List<ChampionSummonerLanguage>>, response: Response<List<ChampionSummonerLanguage>>) {
                    if(response.isSuccessful){
                        val masteryList = response.body()

                        if(masteryList != null){
                            val sortedMasteryList = masteryList.sortedByDescending { it.championPoints ?: 0 }
                            Cache.actualSummonerChampion = sortedMasteryList
                            Cache.adapterM = MasteryAdapter(sortedMasteryList, dbHelper)
                            val searchEditText = binding.searchChampionEditText

                            binding.masteryRecyclerView.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = Cache.adapterM
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
        }
        else {
            Cache.adapterM = MasteryAdapter(Cache.actualSummonerChampion, dbHelper)
            binding.masteryRecyclerView.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = Cache.adapterM
            }
        }

        championFiltre.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                Cache.adapterM?.filterByName(s.toString())
            }

            override fun afterTextChanged(s: Editable) {
            }
        })
        return binding.root
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
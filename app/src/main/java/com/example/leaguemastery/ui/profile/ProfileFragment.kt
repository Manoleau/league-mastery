package com.example.leaguemastery.ui.profile

import android.annotation.SuppressLint
import android.content.Context
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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.leaguemastery.API.ApiClientLeagueMastery
import com.example.leaguemastery.Cache
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.databinding.FragmentProfileBinding
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.example.leaguemastery.entity.Summoner
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
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
        val masteryPointsTextView:TextView = binding.masteryPointsTextView

        if(Cache.actualSummonerChampion.isEmpty()){
            val callMasteryChampion = ApiClientLeagueMastery.api.getSummonerChampionsLanguageByPuuid(Cache.actualSummoner?.puuid!!, Cache.langue.code)
            callMasteryChampion.enqueue(object : Callback<List<ChampionSummonerLanguage>>{
                override fun onResponse(call: Call<List<ChampionSummonerLanguage>>, response: Response<List<ChampionSummonerLanguage>>) {
                    if(response.isSuccessful){
                        val masteryList = response.body()

                        if(masteryList != null){
                            val sortedMasteryList = masteryList.sortedByDescending { it.championPoints ?: 0 }
                            Cache.actualSummonerChampion = sortedMasteryList
                            for(championMas in sortedMasteryList){
                                Cache.actualSummoner?.masteryPoints = Cache.actualSummoner?.masteryPoints?.plus(
                                    championMas.championPoints!!
                                )!!
                            }
                            Cache.adapterM = MasteryAdapter(sortedMasteryList, dbHelper)

                            binding.masteryRecyclerView.apply {
                                layoutManager = LinearLayoutManager(context)
                                adapter = Cache.adapterM
                            }
                            setProfileInViews(context, binding)
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
            setProfileInViews(context, binding)
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
    fun setProfile(profileIconImageView:ImageView, riotNameTextView:TextView, summonerLevelTextView:TextView, masteryPointsTextView:TextView, summoner: Summoner, iconSummoner:Drawable){
        profileIconImageView.setImageDrawable(iconSummoner)
        riotNameTextView.text = "${summoner.riotName}#${summoner.tag}"
        summonerLevelTextView.text = "Level: ${summoner.summonerLevel}"

        masteryPointsTextView.text = "Maîtrise: ${formatNumber(summoner.masteryPoints)} pts"
    }

    fun formatNumber(number: Int): String {
        if (number >= 1_000_000) {
            return String.format("%.1fM", number / 1_000_000.0).replace(Regex("\\.0+"), "")
        } else if (number >= 1_000) {
            return String.format("%.1fK", number / 1_000.0).replace(Regex("\\.0+"), "")
        }
        return number.toString()
    }

    fun setProfileInViews(context: Context, binding: FragmentProfileBinding){
        val handler = Handler(Looper.getMainLooper())
        val profileIconImageView = binding.profileIconImageView
        val riotNameTextView = binding.riotNameTextView
        val summonerLevelTextView = binding.summonerLevelTextView
        val masteryPointsTextView = binding.masteryPointsTextView
        val summoner = Cache.actualSummoner
        var iconSummoner:Drawable?
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
                        setProfile(profileIconImageView, riotNameTextView, summonerLevelTextView, masteryPointsTextView, summoner, iconSummoner!!)
                    }
                }
            }.start()
        }
        else {
            iconSummoner = Cache.data[summoner?.profileIconId.toString()]?.get("image")
            if (summoner?.riotName != null && tag != null && iconSummoner != null) {
                setProfile(profileIconImageView, riotNameTextView, summonerLevelTextView, masteryPointsTextView, summoner, iconSummoner!!)
            }
        }
    }
}
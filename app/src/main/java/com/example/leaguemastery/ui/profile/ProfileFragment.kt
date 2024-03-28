package com.example.leaguemastery.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.leaguemastery.API.ApiClientLeagueMastery
import com.example.leaguemastery.API.Update
import com.example.leaguemastery.Cache
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.R
import com.example.leaguemastery.databinding.FragmentProfileBinding
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val context = root.context
        dbHelper = DBHelper(context, null)
        val championFiltre = binding.searchChampionEditText
        profileIconImageView = binding.profileIconImageView
        riotNameTextView = binding.riotNameTextView
        summonerLevelTextView = binding.summonerLevelTextView
        masteryPointsTextView = binding.masteryPointsTextView
        masteryRecyclerView = binding.masteryRecyclerView
        if(!Update.updating){
            val firebaseAuth = FirebaseAuth.getInstance()
            val currentUser = firebaseAuth.currentUser
            var uid = "0"
            if(currentUser != null){
                uid = currentUser.uid
            }
            var isFav = dbHelper!!.isFav(uid, Cache.actualSummoner!!.puuid)
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
                                Cache.adapterM = MasteryAdapter(sortedMasteryList)
                                binding.masteryRecyclerView.apply {
                                    layoutManager = LinearLayoutManager(context)
                                    adapter = Cache.adapterM
                                }
                                setProfileInViews(context)
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
                Cache.adapterM = MasteryAdapter(Cache.actualSummonerChampion)
                masteryRecyclerView?.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = Cache.adapterM
                }
                setProfileInViews(context)
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
            val favoriteIcon: ImageView = binding.favoriteIcon
            if (isFav){
                favoriteIcon.setImageResource(R.drawable.ic_fav)
            } else {
                favoriteIcon.setImageResource(R.drawable.ic_notfav)
            }
            favoriteIcon.setOnClickListener {
                isFav = if (isFav) {
                    favoriteIcon.setImageResource(R.drawable.ic_notfav)
                    dbHelper!!.removeFav(uid, Cache.actualSummoner!!.puuid)
                    false
                } else {
                    favoriteIcon.setImageResource(R.drawable.ic_fav)
                    dbHelper!!.addFav(uid, "${Cache.actualSummoner!!.riotName}#${Cache.actualSummoner!!.tag}", Cache.actualSummoner!!.puuid, Cache.actualSummoner!!.profileIconId)
                    Toast.makeText(context, "Ajout de ${Cache.actualSummoner?.riotName}#${Cache.actualSummoner?.tag} en favoris", Toast.LENGTH_SHORT).show()
                    true
                }
            }

        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object{
        var profileIconImageView: ImageView? = null
        var riotNameTextView:TextView? = null
        var summonerLevelTextView:TextView? = null
        var masteryPointsTextView:TextView? = null
        var dbHelper:DBHelper? = null
        var masteryRecyclerView:RecyclerView? = null

        /**
         * Met à jour les vues avec les informations de profil de l'invocateur et de sa maîtrise totale des champions.
         *
         * @param iconSummoner L'icône de profil de l'invocateur sous forme de Drawable.
         */
        @SuppressLint("SetTextI18n")
        fun setProfile(iconSummoner: Drawable){
            profileIconImageView?.setImageDrawable(iconSummoner)
            riotNameTextView?.text = "${Cache.actualSummoner?.riotName}#${Cache.actualSummoner?.tag}"
            summonerLevelTextView?.text = "Level: ${Cache.actualSummoner?.summonerLevel}"
            masteryPointsTextView?.text = "Maîtrise: ${formatNumber(Cache.actualSummoner?.masteryPoints!!)} pts"
        }

        /**
         * Formate et retourne une chaîne représentant un nombre avec des suffixes pour les milliers (K) et les millions (M).
         *
         * @param number Le nombre à formater.
         * @return Une chaîne représentant le nombre formaté.
         */
        private fun formatNumber(number: Int): String {
            if (number >= 1_000_000) {
                return String.format("%.1fM", number / 1_000_000.0).replace(Regex("\\.0+"), "")
            } else if (number >= 1_000) {
                return String.format("%.1fK", number / 1_000.0).replace(Regex("\\.0+"), "")
            }
            return number.toString()
        }

        /**
         * Met à jour le profil de l'invocateur dans les vues, y compris son icône, son nom, son niveau et sa maîtrise totale des champions.
         * Si l'icône de l'invocateur n'est pas en cache, la télécharge, puis met à jour les vues.
         *
         * @param context Contexte de l'application.
         */
        fun setProfileInViews(context: Context){
            val handler = Handler(Looper.getMainLooper())
            val summoner = Cache.actualSummoner
            var iconSummoner:Drawable?
            if(Cache.data[summoner?.profileIconId.toString()] == null){
                Thread{
                    iconSummoner = Cache.setImage(
                        "https://ddragon.leagueoflegends.com/cdn/${Cache.version}/img/profileicon/${summoner?.profileIconId}.png",
                        summoner?.profileIconId.toString(),
                        "image",
                        Cache.version,
                        dbHelper!!,
                        context
                    )
                    handler.post{
                        if (iconSummoner != null) {
                            setProfile(iconSummoner!!)
                        }
                    }
                }.start()
            }
            else {
                iconSummoner = Cache.data[summoner?.profileIconId.toString()]?.get("image")
                if (iconSummoner != null) {
                    setProfile(iconSummoner!!)
                }
            }
        }
    }


}
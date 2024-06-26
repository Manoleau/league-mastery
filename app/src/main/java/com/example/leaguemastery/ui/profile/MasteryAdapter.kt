package com.example.leaguemastery.ui.profile

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.leaguemastery.Cache
import com.example.leaguemastery.R
import com.example.leaguemastery.entity.ChampionSummonerLanguage

/**
 * Adapter pour afficher les données de maîtrise des champions dans un RecyclerView.
 */
class MasteryAdapter(private var masteryList: List<ChampionSummonerLanguage>) :
    RecyclerView.Adapter<MasteryAdapter.MasteryViewHolder>() {

    private var filteredMasteryList: List<ChampionSummonerLanguage> = masteryList

    class MasteryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val championName: TextView = view.findViewById(R.id.championNameTextView)
        val championIcon: ImageView = view.findViewById(R.id.championIconImageView)
        val championLevel: ImageView = view.findViewById(R.id.championLevelImageView)
        val championPoints: TextView = view.findViewById(R.id.championPointsTextView)
        val chestGrantedImage: ImageView = view.findViewById(R.id.chestGrantedImageView)
        val chestGrantedText: TextView = view.findViewById(R.id.chestGrantedTextView)
        val context: Context = view.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasteryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mastery_item, parent, false)
        return MasteryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MasteryViewHolder, position: Int) {
        val mastery = filteredMasteryList[position]
        if(Cache.data[mastery.champion.key.toString()] != null && Cache.data[mastery.champion.key.toString()]!!.containsKey("image_icon")) {
            val championIcon = Cache.data[mastery.champion.key.toString()]!!["image_icon"]
            setAll(holder, mastery, championIcon!!)
            holder.itemView.setOnClickListener{
                Toast.makeText(holder.context, mastery.champion.name, Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Configure les informations de maîtrise d'un champion pour un ViewHolder donné.
     * Affecte l'icône, le nom, le niveau de maîtrise, les points de maîtrise et l'état de l'obtention du coffre
     * au ViewHolder.
     *
     * @param holder Le ViewHolder qui doit être configuré.
     * @param mastery L'objet ChampionSummonerLanguage contenant les informations de maîtrise du champion.
     * @param championIcon L'icône du champion à afficher.
     */
    @SuppressLint("SetTextI18n")
    fun setAll(holder:MasteryViewHolder, mastery:ChampionSummonerLanguage, championIcon:Drawable){
        holder.championIcon.setImageDrawable(championIcon)
        holder.championName.text = mastery.champion.name
        when (mastery.championLevel) {
            1 -> {
                holder.championLevel.setImageResource(R.drawable.mastery1)
            }
            2 -> {
                holder.championLevel.setImageResource(R.drawable.mastery2)
            }
            3 -> {
                holder.championLevel.setImageResource(R.drawable.mastery3)
            }
            4 -> {
                holder.championLevel.setImageResource(R.drawable.mastery4)
            }
            5 -> {
                holder.championLevel.setImageResource(R.drawable.mastery5)
            }
            6 -> {
                holder.championLevel.setImageResource(R.drawable.mastery6)
            }
            7 -> {
                holder.championLevel.setImageResource(R.drawable.mastery7)
            }
            else -> {
                holder.championLevel.setImageResource(R.drawable.mastery1)
            }
        }
        if(mastery.chestGranted == true){
            holder.chestGrantedImage.setImageResource(R.drawable.chestclaimed)
            holder.chestGrantedText.text = "Obtenue"
        } else{
            holder.chestGrantedImage.setImageResource(R.drawable.chest)
            holder.chestGrantedText.text = "Non Obtenue"
        }
        holder.championPoints.text = mastery.championPoints.toString()
    }

    override fun getItemCount() = filteredMasteryList.size

    /**
     * Met à jour la liste des maîtrises des champions avec de nouvelles données et notifie
     * le RecyclerView que les données ont changé.
     *
     * @param newMasteryList La nouvelle liste des maîtrises des champions à afficher.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun loadMastery(newMasteryList: List<ChampionSummonerLanguage>) {
        filteredMasteryList = newMasteryList
        notifyDataSetChanged()
    }

    /**
     * Filtre la liste des maîtrises des champions par nom de champion et notifie le RecyclerView
     * que les données ont changé.
     *
     * @param name Le nom du champion à filtrer.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun filterByName(name: String) {
        filteredMasteryList = if (name.isEmpty()) {
            masteryList
        } else {

            masteryList.filter {
                it.champion.name.lowercase().contains(name.lowercase() ) or
                        it.champion.name_id!!.lowercase().contains(name.lowercase())
            }
        }
        notifyDataSetChanged()
    }

}

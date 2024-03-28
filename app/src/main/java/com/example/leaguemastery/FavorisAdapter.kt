package com.example.leaguemastery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.leaguemastery.DB.FavDB
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.example.leaguemastery.ui.profile.MasteryAdapter

class FavorisAdapter(private var favorisList: List<FavDB>):
    RecyclerView.Adapter<FavorisAdapter.FavorisViewHolder>(){

    class FavorisViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileIcon: ImageView = view.findViewById(R.id.profileIconImageView)
        val riotName: TextView = view.findViewById(R.id.riotNameFavTextView)
        val context = view.context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavorisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favoris_item, parent, false)
        return FavorisViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favorisList.size
    }

    override fun onBindViewHolder(holder: FavorisViewHolder, position: Int) {
        val favoris = favorisList[position]
        holder.profileIcon.setImageDrawable(favoris.image)
        holder.riotName.text = favoris.riotacc
        holder.itemView.setOnClickListener{
            AcceuilRecherche.riotAccText.setText(favoris.riotacc)
            AcceuilRecherche.searchPlayer(holder.context)
        }
    }
    fun loadMastery(newfavorisList: List<FavDB>) {
        favorisList = newfavorisList
        notifyDataSetChanged()
    }

}
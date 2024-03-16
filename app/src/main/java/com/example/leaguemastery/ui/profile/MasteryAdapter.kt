package com.example.leaguemastery.ui.profile

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.leaguemastery.ImagesDrawable
import com.example.leaguemastery.R
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import java.util.Locale

class MasteryAdapter(private val masteryList: List<ChampionSummonerLanguage>) :
    RecyclerView.Adapter<MasteryAdapter.MasteryViewHolder>(), Filterable {

    class MasteryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val championName: TextView = view.findViewById(R.id.championNameTextView)
        val championIcon: ImageView = view.findViewById(R.id.championIconImageView)
        val championLevel: ImageView = view.findViewById(R.id.championLevelImageView)
        val championPoints: TextView = view.findViewById(R.id.championPointsTextView)
        val chestGrantedImage: ImageView = view.findViewById(R.id.chestGrantedImageView)
        val chestGrantedText: TextView = view.findViewById(R.id.chestGrantedTextView)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasteryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mastery_item, parent, false)
        return MasteryViewHolder(view)
    }

    override fun onBindViewHolder(holder: MasteryViewHolder, position: Int) {

        val handler = Handler(Looper.getMainLooper())
        val mastery = masteryList[position]
        if(ImagesDrawable.data[mastery.champion.image_icon.toString()] == null) {
            Thread{
                val championIcon = ImagesDrawable.setImage(mastery.champion.image_icon.toString(), mastery.champion.key.toString(), "image_icon")
                handler.post{
                    setAll(holder, mastery, championIcon!!, position)
                }
            }.start()
        }
        else{
            val championIcon = ImagesDrawable.data[mastery.champion.image_icon.toString()]?.get("image_icon")
            setAll(holder, mastery, championIcon!!, position)
        }

    }


    fun setAll(holder:MasteryViewHolder, mastery:ChampionSummonerLanguage, championIcon:Drawable, position:Int){
        //if (position == 0){
        //    holder.itemView.setBackgroundResource(R.drawable.bottom_border)
        //} else if(position == itemCount-1){
        //    holder.itemView.setBackgroundResource(R.drawable.bottom_top_border)
        //} else {
        //    holder.itemView.setBackgroundResource(R.drawable.top_border)
        // }
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
    override fun getItemCount() = masteryList.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                masteryListFilter = if (charSearch.isEmpty()) {
                    masteryList
                } else {
                    val resultList = ArrayList<ChampionSummonerLanguage>()
                    for (row in masteryList) {
                        val name_id:String = row.champion.name_id!!
                        if (row.champion.name.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT)) or name_id.toLowerCase(Locale.ROOT).contains(charSearch.toLowerCase(Locale.ROOT))) {
                            resultList.add(row)
                        }
                    }
                    resultList
                }
                return FilterResults().apply { values = masteryListFilter }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                masteryListFilter = results?.values as ArrayList<ChampionSummonerLanguage>
                notifyDataSetChanged()
            }
        }
    }


}

package com.example.leaguemastery.ui.home

import android.R
import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leaguemastery.API.ApiClient
import com.example.leaguemastery.databinding.FragmentHomeBinding
import com.example.leaguemastery.entity.ChampionAbstract
import com.example.leaguemastery.entity.ChampionDefault
import com.example.leaguemastery.entity.ChampionLanguage
import com.example.leaguemastery.entity.Language
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.URL

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var champions:List<ChampionDefault>
    private var championsName:ArrayList<String> = ArrayList()
    var image_icon_champ: Drawable? = null
    var image_load_screen_champ: Drawable? = null
    var image_splash_champ: Drawable? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val champEditText = binding.champEditText
        val btn = binding.tktbtn

        setAutoCompleteTextView(champEditText)

        btn.setOnClickListener{
            setOnClickSearchChamp(champEditText.text.toString())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setOnClickSearchChamp(championRechercher:String){
        val call = ApiClient.api.getChampionByNameIdWithLanguage(championRechercher, Language.FR_FR.code)
        call.enqueue(object : Callback<ChampionLanguage> {
            override fun onResponse(call: Call<ChampionLanguage>, response: Response<ChampionLanguage>) {
                if (response.isSuccessful) {
                    val champ = response.body()
                    if (champ != null) {
                        var msg_roles = ""
                        val roles = champ.roles
                        if (roles != null) {
                            for(role in roles){
                                msg_roles += role?.default_name + " "
                            }
                        }
                        val builder = AlertDialog.Builder(context)
                        builder.setTitle("Champion ${champ.name}")
                        builder.setMessage("Nom : ${champ.name}\nTitre : ${champ.title}\nRoles : $msg_roles")
                        setImages(champ)
                        FetchImages(champ, this@HomeFragment).start()
                        builder.setIcon(image_icon_champ)
                        builder.setPositiveButton("OK") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                } else {
                    Toast.makeText(context, "Aucun résultat", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ChampionLanguage>, t: Throwable) {
                Toast.makeText(context, "Aucun résultat", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView){
        val callChampions = ApiClient.api.getChampions()
        callChampions.enqueue(object : Callback<List<ChampionDefault>> {
            override fun onResponse(call: Call<List<ChampionDefault>>, response: Response<List<ChampionDefault>>) {
                if (response.isSuccessful) {
                    champions = response.body()!!
                    championsName = ArrayList()
                    for(champion in champions){
                        championsName.add(champion.name_id)
                    }
                    val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter<String>(it, R.layout.simple_list_item_1, championsName) }
                    autoCompleteTextView.setAdapter(adapter)
                } else {
                    autoCompleteTextView.setAdapter(null)
                }
            }

            override fun onFailure(call: Call<List<ChampionDefault>>, t: Throwable) {
                autoCompleteTextView.setAdapter(null)
            }
        })
    }



    fun setImages(champ: ChampionAbstract?){
        if (champ != null) {
            try {
                val image_icon = URL(champ.image_icon).content as InputStream
                image_icon_champ = Drawable.createFromStream(image_icon, "image_icon")
                val image_load_screen = URL(champ.image_load_screen).content as InputStream
                image_load_screen_champ = Drawable.createFromStream(image_load_screen, "image_load_screen")
                val image_splash = URL(champ.image_splash).content as InputStream
                image_splash_champ = Drawable.createFromStream(image_splash, "image_splash")
            } catch (e: Exception) {
                Log.e("Erreur Image", e.toString())
            }
        }
    }
    class FetchImages internal constructor(var champ: ChampionAbstract?, var homeFragment: HomeFragment) : Thread() {
        override fun run() {
            homeFragment.setImages(champ)
        }
    }



}
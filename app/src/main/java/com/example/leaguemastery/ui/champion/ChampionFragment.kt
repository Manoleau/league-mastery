package com.example.leaguemastery.ui.champion

import android.R
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.leaguemastery.API.ApiClient
import com.example.leaguemastery.ImagesDrawable
import com.example.leaguemastery.databinding.FragmentChampionBinding
import com.example.leaguemastery.entity.ChampionAbstract
import com.example.leaguemastery.entity.ChampionDefault
import com.example.leaguemastery.entity.ChampionLanguage
import com.example.leaguemastery.entity.Language
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.URL

class ChampionFragment : Fragment() {
    private var _binding: FragmentChampionBinding? = null
    private val binding get() = _binding!!
    private lateinit var champions:List<ChampionDefault>
    private var championsName:ArrayList<String> = ArrayList()
    var image_icon_champ: Drawable? = null
    var image_load_screen_champ: Drawable? = null
    var image_splash_champ: Drawable? = null
    var image_roles: ArrayList<Drawable> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentChampionBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val champEditText = binding.champEditText
        val btn = binding.btnSearchChamp
        val colonneChamp = binding.colonneChamp
        val colonneTitre = binding.colonneTitre
        val colonneRoles = binding.colonneRoles

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
                        val handler = Handler(Looper.getMainLooper())
                        image_roles = ArrayList()
                        val threads = mutableListOf<Thread>()
                        val roles = champ.roles
                        if (roles != null) {
                            for(role in roles){
                                if(!ImagesDrawable.data.containsKey(role?._id)){
                                    val thread = Thread{
                                        setImageRole(role?._id, role?.image_icon)
                                    }
                                    thread.start()
                                    threads.add(thread)

                                } else {
                                    ImagesDrawable.data.get(role?._id)?.get("image")?.let { image_roles.add(it) }
                                }
                            }
                            for (thread in threads) {
                                thread.join()
                            }
                        }
                        handler.post{
                            if(!ImagesDrawable.data.containsKey(champ.key.toString())){
                                ImagesDrawable.data.put(champ.key.toString(), HashMap())
                                Thread {
                                    setImagesChamp(champ)
                                    handler.post {

                                        val builder = getBuilderNewChamp(champ)
                                        builder.show()
                                    }
                                }.start()
                            } else {
                                image_icon_champ = ImagesDrawable.data.get(champ.key.toString())?.get("image_icon")
                                image_splash_champ = ImagesDrawable.data.get(champ.key.toString())?.get("image_splash")
                                image_load_screen_champ = ImagesDrawable.data.get(champ.key.toString())?.get("image_load_screen")
                                val builder = getBuilderNewChamp(champ)
                                builder.show()
                            }
                        }

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
    fun setImagesChamp(champ: ChampionAbstract?){
        if (champ != null) {
            try {
                image_icon_champ = null
                image_load_screen_champ = null
                image_splash_champ = null
                val image_icon = URL(champ.image_icon).content as InputStream
                image_icon_champ = Drawable.createFromStream(image_icon, "image_icon")
                val image_load_screen = URL(champ.image_load_screen).content as InputStream
                image_load_screen_champ = Drawable.createFromStream(image_load_screen, "image_load_screen")
                val image_splash = URL(champ.image_splash).content as InputStream
                image_splash_champ = Drawable.createFromStream(image_splash, "image_splash")
                if(image_icon_champ != null){
                    ImagesDrawable.data.get(champ.key.toString())?.put("image_icon",
                        image_icon_champ!!
                    )
                }
                if(image_splash_champ != null){
                    ImagesDrawable.data.get(champ.key.toString())?.put("image_splash",
                        image_splash_champ!!
                    )
                }
                if(image_load_screen_champ != null){
                    ImagesDrawable.data.get(champ.key.toString())?.put("image_load_screen",
                        image_load_screen_champ!!
                    )
                }
            } catch (e: Exception) {
                Log.e("Erreur Image", e.toString())
            }
        }
    }
    fun setImageRole(id:String?,url: String?){
        if (url != null && id != null) {
            try {
                val image = URL(url).content as InputStream
                val imagerole = Drawable.createFromStream(image, "image")
                if (imagerole != null) {
                    image_roles.add(imagerole)
                    ImagesDrawable.data.get(id)?.put("image", imagerole)
                }
            } catch (e: Exception) {
                Log.e("Erreur Image", e.toString())
            }
        }
    }
    fun getBuilderNewChamp(champ:ChampionLanguage): AlertDialog.Builder{
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
        builder.setIcon(image_icon_champ)
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        return builder
    }
    fun getTableRow(champ: ChampionLanguage, colonneChamp:LinearLayout, colonneTitre:LinearLayout, colonneRoles:LinearLayout){
        colonneChamp.removeAllViews()
        val textViewName = TextView(context).apply {
            text = champ.name
            setPadding(3, 3, 3, 3)
        }
        val imageView = ImageView(context).apply {
            setImageResource(image_icon_champ?)
        }

        val attributes = arrayOf("Attribut 1", "Attribut 2", "Attribut 3")
        for (attribute in attributes) {
            val textView = TextView(context).apply {
                text = attribute
                setPadding(3, 3, 3, 3)
            }
            tableRow.addView(textView)
        }

    }
}
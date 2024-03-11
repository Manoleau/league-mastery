package com.example.leaguemastery.ui.champion

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import java.util.concurrent.CountDownLatch

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
        colonneChamp.gravity = Gravity.CENTER_HORIZONTAL
        colonneTitre.gravity = Gravity.CENTER_HORIZONTAL
        colonneRoles.gravity = Gravity.CENTER_HORIZONTAL
        setAutoCompleteTextView(champEditText)


        btn.setOnClickListener{
            setOnClickSearchChamp(champEditText.text.toString(), binding)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun setChampions(latchOrigin:CountDownLatch){
        val latch = CountDownLatch(champions.size)

        for (champion in champions) {
            Thread {
                try {
                    setImagesChamp(champion)
                } finally {
                    latch.countDown()
                    Log.i("Champion", "${champion.name_id} : ${latch.count}")

                }
            }.start()
        }
        Thread {
            latch.await()
            latchOrigin.countDown()
            Log.i("Images", ImagesDrawable.data.toString())
        }.start()
    }
    private fun setOnClickSearchChamp(championRechercher:String, binding: FragmentChampionBinding){
        binding.progressBar.visibility = View.VISIBLE

        val call = ApiClient.api.getChampionByNameIdWithLanguage(championRechercher, Language.FR_FR.code)
        call.enqueue(object : Callback<ChampionLanguage> {
            override fun onResponse(call: Call<ChampionLanguage>, response: Response<ChampionLanguage>) {
                if (response.isSuccessful) {
                    val champ = response.body()
                    if (champ != null) {
                        val handler = Handler(Looper.getMainLooper())
                        image_roles = ArrayList()
                        val roles = champ.roles
                        if (roles != null) {
                            for(role in roles){
                                if(!ImagesDrawable.data.containsKey(role?._id)){
                                    Thread{
                                        setImageRole(role?._id, role?.image_icon)
                                    }.start()

                                } else {
                                    ImagesDrawable.data.get(role?._id)?.get("image")?.let { image_roles.add(it) }
                                }
                            }
                        }
                        handler.post{

                            if(!ImagesDrawable.data.containsKey(champ.key.toString())){
                                ImagesDrawable.data.put(champ.key.toString(), HashMap())
                                Thread {
                                    setImagesChamp(champ)
                                    handler.post {
                                        //val builder = getBuilderNewChamp(champ)
                                        //builder.show()
                                        setTableRow(champ, binding)
                                        binding.progressBar.visibility = View.GONE

                                    }
                                }.start()
                            } else {
                                image_icon_champ = ImagesDrawable.data.get(champ.key.toString())?.get("image_icon")
                                image_splash_champ = ImagesDrawable.data.get(champ.key.toString())?.get("image_splash")
                                image_load_screen_champ = ImagesDrawable.data.get(champ.key.toString())?.get("image_load_screen")
                                //val builder = getBuilderNewChamp(champ)
                                //builder.show()
                                setTableRow(champ, binding)
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Aucun résultat", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
            override fun onFailure(call: Call<ChampionLanguage>, t: Throwable) {
                Toast.makeText(context, "Aucun résultat", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
            }
        })
    }
    private fun setAutoCompleteTextView(autoCompleteTextView: AutoCompleteTextView){
        val callChampions = ApiClient.api.getChampions()
        Log.i("Chargement", "Départ")

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
                    //val latch = CountDownLatch(1)

                    //Thread{
                    //    setChampions(latch)
                    //}.start()
                    //Thread{
                    //    latch.await()
                    //    Log.i("Chargement", "Fini")
                    // }

                } else {
                    champions = emptyList()
                    autoCompleteTextView.setAdapter(null)
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<ChampionDefault>>, t: Throwable) {
                champions = emptyList()
                autoCompleteTextView.setAdapter(null)
                binding.progressBar.visibility = View.GONE
            }
        })
    }
    fun setImagesChamp(champ: ChampionAbstract?){
        if (champ != null) {
            try {
                var image_icon_champ: Drawable? = null
                var image_load_screen_champ: Drawable? = null
                var image_splash_champ: Drawable? = null
                val image_icon = URL(champ.image_icon).content as InputStream
                image_icon_champ = Drawable.createFromStream(image_icon, "image_icon")
                val image_load_screen = URL(champ.image_load_screen).content as InputStream
                image_load_screen_champ = Drawable.createFromStream(image_load_screen, "image_load_screen")
                val image_splash = URL(champ.image_splash).content as InputStream
                image_splash_champ = Drawable.createFromStream(image_splash, "image_splash")
                if(image_icon_champ != null){
                    ImagesDrawable.data.get(champ.key.toString())?.put("image_icon",
                        image_icon_champ
                    )
                }
                if(image_splash_champ != null){
                    ImagesDrawable.data.get(champ.key.toString())?.put("image_splash",
                        image_splash_champ
                    )
                }
                if(image_load_screen_champ != null){
                    ImagesDrawable.data.get(champ.key.toString())?.put("image_load_screen",
                        image_load_screen_champ
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
                    ImagesDrawable.data.put(id, HashMap())
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
    fun setTableRow(champ: ChampionLanguage, binding:FragmentChampionBinding){
        val colonneRoles = binding.colonneRoles
        colonneRoles.removeAllViews()

        binding.textViewChamp.text = champ.name

        val imageViewIconChamp = binding.imageViewIconChamp
        imageViewIconChamp.layoutParams = LinearLayout.LayoutParams(200, 200).apply {
            gravity = Gravity.CENTER
        }
        imageViewIconChamp.scaleType = ImageView.ScaleType.FIT_CENTER
        imageViewIconChamp.setImageDrawable(image_icon_champ)

        binding.textViewTitle.text = champ.title

        for(role in champ.roles!!){
            val linearLayout = LinearLayout(context).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }
            }
            val textViewRole = TextView(context).apply {
                text = role?.default_name
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.CENTER
                }
                context?.let {
                    setTextColor(ContextCompat.getColor(it, R.color.black))
                }
                setPadding(3, 3, 3, 3)
            }
            val imageViewRole = ImageView(context).apply {
                scaleType = ImageView.ScaleType.FIT_CENTER

                setImageDrawable(ImagesDrawable.data.get(role?._id)?.get("image"))
            }
            linearLayout.addView(imageViewRole)
            linearLayout.addView(textViewRole)
            colonneRoles.addView(linearLayout)
        }
    }
}
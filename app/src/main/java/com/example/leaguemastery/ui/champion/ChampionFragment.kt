package com.example.leaguemastery.ui.champion

//noinspection SuspiciousImport
import android.R
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
    var championActual: ChampionLanguage? = null
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
    private fun setChampions(){
        val latch = CountDownLatch(champions.size)
        for (champion in champions) {
            Thread {
                try {
                    setImagesChamp()
                } finally {
                    latch.countDown()
                    Log.i("Champion", "${champion.name_id} : ${latch.count}")

                }
            }.start()
        }
        Thread {
            latch.await()
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
                        championActual = champ

                        val roles = championActual!!.roles
                        if (roles != null) {
                            for(role in roles){
                                if(!ImagesDrawable.data.containsKey(role?._id)){
                                    Thread{
                                        setImageRole(role?._id, role?.image_icon)
                                    }.start()
                                }
                            }
                        }
                        handler.post{
                            if(!ImagesDrawable.data.containsKey(championActual!!.key.toString())){
                                ImagesDrawable.data[championActual!!.key.toString()] = HashMap()
                                Thread {
                                    setImagesChamp()
                                    handler.post {
                                        val builder = getBuilderNewChamp()
                                        builder.show()
                                        setTableRow(binding)

                                        binding.progressBar.visibility = View.GONE

                                    }
                                }.start()
                            } else {
                                //val builder = getBuilderNewChamp(champ)
                                //builder.show()
                                setTableRow(binding)
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

        callChampions.enqueue(object : Callback<List<ChampionDefault>> {
            override fun onResponse(call: Call<List<ChampionDefault>>, response: Response<List<ChampionDefault>>) {
                if (response.isSuccessful) {
                    champions = response.body()!!
                    setChampions()
                    championsName = ArrayList()
                    for(champion in champions){
                        championsName.add(champion.name_id)
                    }
                    val adapter: ArrayAdapter<String>? = context?.let { ArrayAdapter<String>(it, R.layout.simple_list_item_1, championsName) }
                    autoCompleteTextView.setAdapter(adapter)
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
    fun setImagesChamp(){
        try {
            val imageIconChamp: Drawable?
            val imageLoadScreenChamp: Drawable?
            val imageSplashChamp: Drawable?
            val imageIcon = URL(championActual!!.image_icon).content as InputStream
            imageIconChamp = Drawable.createFromStream(imageIcon, "image_icon")
            val imageLoadScreen = URL(championActual!!.image_load_screen).content as InputStream
            imageLoadScreenChamp = Drawable.createFromStream(imageLoadScreen, "image_load_screen")
            val imageSplash = URL(championActual!!.image_splash).content as InputStream
            imageSplashChamp = Drawable.createFromStream(imageSplash, "image_splash")

            if(imageIconChamp != null){
                ImagesDrawable.data[championActual!!.key.toString()]?.put("image_icon",
                    imageIconChamp
                )
            }
            if(imageSplashChamp != null){
                ImagesDrawable.data[championActual!!.key.toString()]?.put("image_splash",
                    imageSplashChamp
                )
            }
            if(imageLoadScreenChamp != null){
                ImagesDrawable.data[championActual!!.key.toString()]?.put("image_load_screen",
                    imageLoadScreenChamp
                )
            }

        } catch (e: Exception) {
            Log.e("Erreur Image", e.toString())
        }
    }
    fun setImageRole(id:String?,url: String?){
        if (url != null && id != null) {
            try {
                val image = URL(url).content as InputStream
                val imagerole = Drawable.createFromStream(image, "image")
                if (imagerole != null) {
                    ImagesDrawable.data[id] = HashMap()
                    ImagesDrawable.data[id]?.put("image", imagerole)
                }
            } catch (e: Exception) {
                Log.e("Erreur Image", e.toString())
            }
        }
    }
    fun getBuilderNewChamp(): AlertDialog.Builder{
        var msgRoles = ""
        val roles = championActual?.roles
        if (roles != null) {
            for(role in roles){
                msgRoles += role?.default_name + " "
            }
        }
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Champion ${championActual?.name}")
        builder.setMessage("Nom : ${championActual?.name}\nTitre : ${championActual?.title}\nRoles : $msgRoles")
        builder.setIcon(ImagesDrawable.data[championActual?.key.toString()]?.get("image_icon"))
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }
        return builder
    }
    fun setTableRow(binding:FragmentChampionBinding){
        val colonneRoles = binding.colonneRoles
        colonneRoles.removeAllViews()
        binding.textViewChamp.text = championActual?.name

        val imageViewIconChamp = binding.imageViewIconChamp
        imageViewIconChamp.layoutParams = LinearLayout.LayoutParams(200, 200).apply {
            gravity = Gravity.CENTER
        }
        imageViewIconChamp.scaleType = ImageView.ScaleType.FIT_CENTER
        imageViewIconChamp.setImageDrawable(ImagesDrawable.data[championActual?.key.toString()]?.get("image_icon"))

        binding.textViewTitle.text = championActual?.title

        for(role in championActual?.roles!!){
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
                setImageDrawable(ImagesDrawable.data[role?._id]?.get("image"))
            }
            linearLayout.addView(imageViewRole)
            linearLayout.addView(textViewRole)
            colonneRoles.addView(linearLayout)
        }
    }
}
package com.example.leaguemastery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Base64
import android.util.Log
import com.example.leaguemastery.API.ApiClientLeagueMastery
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.entity.ChampionDefault
import com.example.leaguemastery.entity.ChampionSummonerLanguage
import com.example.leaguemastery.entity.Language
import com.example.leaguemastery.entity.Summoner
import com.example.leaguemastery.ui.profile.MasteryAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

/**
 * Classe de gestion de cache pour l'application League Mastery.
 * Cette classe permet de stocker et de gérer les données de manière efficace
 * pour réduire les appels réseau et améliorer les performances de l'application.
 */
class Cache {
    companion object {
        var version = ""
        var langue: Language = Language.FR_FR
        var data = HashMap<String, HashMap<String, Drawable>>()
        var actualSummoner:Summoner? = null
        var actualSummonerChampion: List<ChampionSummonerLanguage> = ArrayList()
        var adapterM: MasteryAdapter? = null

        /**
         * Télécharge une image depuis une URL, la stocke dans le cache et la base de données, et retourne un Drawable.
         * Si l'image est déjà dans la version actuelle, elle est récupérée depuis la base de données.
         *
         * @param url URL de l'image à télécharger.
         * @param key Clé principale pour identifier l'image.
         * @param key2 Clé secondaire pour identifier l'image.
         * @param version Version actuelle des données.
         * @param dbHelper Instance de DBHelper pour interagir avec la base de données.
         * @param context Contexte de l'application.
         * @return Drawable correspondant à l'image téléchargée ou null en cas d'erreur.
         */
        fun setImage(url:String, key:String, key2:String, version:String, dbHelper: DBHelper, context: Context): Drawable?{
            try {
                val oldVer = dbHelper.getVersionImage(key, key2)
                if(oldVer != version){
                    val image: Drawable?
                    val imageIcon = URL(url).content as InputStream
                    image = Drawable.createFromStream(imageIcon, key2)

                    Log.i("imageGGEZ", image.toString())

                    if(image != null){
                        if(data[key] == null){
                            data[key] = HashMap()
                        }
                        data[key]?.put(key2,
                            image
                        )
                        dbHelper.addOrUpgradeImage(key, key2, drawableToBase64(image), version)
                    }
                    return image
                } else {
                    val image = dbHelper.getImage(key, key2)
                    if (image != null){
                        val drawImage = base64ToDrawable(image, context)
                        if(data[key] == null){
                            data[key] = HashMap()
                        }
                        data[key]?.put(key2,
                            drawImage
                        )
                        return drawImage
                    }
                    return null
                }
            } catch (e: Exception) {
                Log.e("Erreur Image", e.toString())
                return null
            }
        }

        /**
         * Convertit un Drawable en une chaîne de caractères Base64.
         *
         * @param drawable Drawable à convertir.
         * @return Chaîne de caractères Base64 représentant le Drawable.
         */
        private fun drawableToBase64(drawable: Drawable): String {
            val bitmap = if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888).also {
                    val canvas = Canvas(it)
                    drawable.setBounds(0, 0, canvas.width, canvas.height)
                    drawable.draw(canvas)
                }
            }
            ByteArrayOutputStream().use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                val byteArray = outputStream.toByteArray()
                return Base64.encodeToString(byteArray, Base64.DEFAULT)
            }
        }

        /**
         * Convertit une chaîne de caractères Base64 en un Drawable.
         *
         * @param base64Str Chaîne de caractères Base64 à convertir.
         * @param context Contexte de l'application.
         * @return Drawable correspondant à la chaîne de caractères Base64.
         */
        fun base64ToDrawable(base64Str: String, context: Context): Drawable {
            val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            return BitmapDrawable(context.resources, bitmap)
        }

        /**
         * Télécharge et met à jour les images des champions depuis une API externe,
         * et met à jour la base de données locale si nécessaire.
         *
         * @param context Contexte de l'application.
         * @param dbHelper Instance de DBHelper pour interagir avec la base de données.
         */
        fun downloadAndSetImages(context: Context, dbHelper: DBHelper){
            val callChampions = ApiClientLeagueMastery.api.getChampions()
            callChampions.enqueue(object : Callback<List<ChampionDefault>>{
                override fun onResponse(
                    call: Call<List<ChampionDefault>>,
                    response: Response<List<ChampionDefault>>
                ) {
                    if(response.isSuccessful){
                        val champions = response.body()
                        data = HashMap()
                        if (champions != null) {
                            for(champion in champions){
                                Thread{
                                    Log.i("Champions", "${champion.default_name} : ${champion.image_icon}")
                                    setImage("https://ddragon.leagueoflegends.com/cdn/$version/img/champion/${champion.name_id}.png", champion.key.toString(), "image_icon", version, dbHelper, context)
                                }.start()
                            }
                        }
                    } else {
                        Log.i("Erreur champions", response.message()+ " " + response.code())
                    }
                }
                override fun onFailure(call: Call<List<ChampionDefault>>, t: Throwable) {
                    Log.i("Erreur champions", t.toString())
                }
            })
        }

        /**
         * Vérifie si l'appareil est actuellement connecté à Internet.
         *
         * @param context Contexte de l'application.
         * @return true si l'appareil est connecté à Internet, false sinon.
         */
        fun isOnline(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }
    }
}
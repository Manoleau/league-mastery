package com.example.leaguemastery

import android.graphics.drawable.Drawable
import android.util.Log
import java.io.InputStream
import java.net.URL

class ImagesDrawable {
    companion object {
        val data = HashMap<String, HashMap<String, Drawable>>()
        fun setImage(url:String, key:String, key2:String):Drawable?{
            try {
                val image: Drawable?
                val imageIcon = URL(url).content as InputStream
                image = Drawable.createFromStream(imageIcon, key2)

                if(image != null){
                    if(data[key] == null){
                        data[key] = HashMap()
                    }
                    data[key]?.put(key2,
                        image
                    )
                }
                return image

            } catch (e: Exception) {
                Log.e("Erreur Image", e.toString())
                return null
            }
        }
    }
}
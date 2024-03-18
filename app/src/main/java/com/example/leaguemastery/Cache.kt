package com.example.leaguemastery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.Log
import com.example.leaguemastery.DB.DBHelper
import com.example.leaguemastery.entity.Language
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

class Cache {
    companion object {
        var version = ""
        var langue: Language = Language.FR_FR
        var data = HashMap<String, HashMap<String, Drawable>>()
        fun setImage(url:String, key:String, key2:String): Drawable?{
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
        fun base64ToDrawable(base64Str: String, context: Context): Drawable {
            val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            return BitmapDrawable(context.resources, bitmap)
        }
        fun saveInPhone(dbHelper: DBHelper){
            data.forEach { (key, innerMap) ->
                innerMap.forEach { (innerKey, drawable) ->
                    dbHelper.addOrUpgradeImage(key, innerKey, drawableToBase64(drawable), version)
                }
            }
        }
    }
}
package com.example.leaguemastery.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.drawable.Drawable
import com.example.leaguemastery.Cache

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME_RIOT_ACC + " ("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                riotAcc + " VARCHAR(255) NOT NULL," +
                puuid + " VARCHAR(255) NOT NULL" +
                ");")
        val query2 = ("CREATE TABLE " + TABLE_NAME_IMAGE + " ("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                key1 + " VARCHAR(255) NOT NULL," +
                key2 + " VARCHAR(255) NOT NULL," +
                version + " VARCHAR(255) NOT NULL," +
                image + " VARCHAR(255) NOT NULL" +
                ");")
        val query3 = ("CREATE TABLE " + TABLE_NAME_FAV_ACC + " ("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                uid + " VARCHAR(255) NOT NULL," +
                riotAcc + " VARCHAR(255) NOT NULL," +
                puuid + " VARCHAR(255) NOT NULL," +
                image + " VARCHAR(255) NOT NULL" +
                ");")
        db.execSQL(query)
        db.execSQL(query2)
        db.execSQL(query3)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_RIOT_ACC")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_IMAGE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME_FAV_ACC")
        onCreate(db)
    }
    fun addFav(uidDB : String, riotAccDB: String, puuidDB: String, profileIconId:Int){
        val values = ContentValues()
        values.put(uid, uidDB)
        values.put(riotAcc, riotAccDB)
        values.put(puuid, puuidDB)
        values.put(image, getImage(profileIconId.toString(), "image"))
        val db = this.writableDatabase
        db.insert(TABLE_NAME_FAV_ACC, null, values)
    }
    fun removeFav(uidDB: String, puuidDB: String):Int{
        val db = this.writableDatabase

        val selection = "uid = ? AND puuid = ?"
        val selectionArgs = arrayOf(uidDB, puuidDB)

        return db.delete(TABLE_NAME_FAV_ACC, selection, selectionArgs)
    }

    fun getFavs(uidDB: String, context: Context):ArrayList<FavDB>{
        val db = this.readableDatabase
        val res = ArrayList<FavDB>()
        val projection = arrayOf("id", "uid", "riotacc", "puuid", "image")
        val selection = "uid = ?"
        val selectionArgs = arrayOf(uidDB)
        val cursor:Cursor = db.query(
            TABLE_NAME_FAV_ACC,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val uid = cursor.getString(1)
                val riotacc = cursor.getString(2)
                val puuid = cursor.getString(3)
                val image = cursor.getString(4)
                res.add(FavDB(id, uid, riotacc, puuid, Cache.base64ToDrawable(image, context)))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res
    }

    fun isFav(uidDB: String, puuidDB: String):Boolean{
        val db = this.readableDatabase
        val projection = arrayOf("id")
        val selection = "uid = ? AND puuid = ?"
        val selectionArgs = arrayOf(uidDB, puuidDB)
        val cursor:Cursor = db.query(
            TABLE_NAME_FAV_ACC,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        val res = cursor.moveToFirst()
        cursor.close()
        return res
    }

    fun updateSummoner(riotAccDB: String, puuidDB: String, profileIconId:Int){
        val db = this.writableDatabase
        val valuesSummCache = ContentValues()
        valuesSummCache.put(riotAcc, riotAccDB)
        val valuesFavs = ContentValues()
        valuesFavs.put(riotAcc, riotAccDB)
        valuesFavs.put(image, getImage(profileIconId.toString(), "image"))
        val selection = "$puuid = ?"
        val selectionArgs = arrayOf(puuidDB)
        db.update(TABLE_NAME_RIOT_ACC, valuesSummCache, selection, selectionArgs)
        db.update(TABLE_NAME_FAV_ACC, valuesFavs, selection, selectionArgs)
    }

    fun addSummoner(riotAccDB : String, puuidDB:String){
        if(getSummoner(riotAccDB) == null){
            val values = ContentValues()
            values.put(riotAcc, riotAccDB)
            values.put(puuid, puuidDB)
            val db = this.writableDatabase
            db.insert(TABLE_NAME_RIOT_ACC, null, values)
        }
    }
    fun removeSummoner(idDB : Int):Int{
        val db = this.writableDatabase

        val selection = "id = ?"
        val selectionArgs = arrayOf(idDB.toString())

        return db.delete(TABLE_NAME_RIOT_ACC, selection, selectionArgs)
    }
    fun getSummoner(riotAccDB: String): SummonerDB? {
        val db = this.readableDatabase
        var res: SummonerDB? = null
        val projection = arrayOf("id", "riotacc", "puuid")
        val selection = "riotacc = ?"
        val selectionArgs = arrayOf(riotAccDB)
        val cursor:Cursor = db.query(
            TABLE_NAME_RIOT_ACC,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(0)
                val riotAcc = cursor.getString(1)
                val puuid = cursor.getString(2)
                res = SummonerDB(id, riotAcc, puuid)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res

    }
    @SuppressLint("Range")
    fun getAllSummoner(): ArrayList<SummonerDB> {
        val db = this.readableDatabase
        val res = ArrayList<SummonerDB>()
        val projection = arrayOf("id", "riotacc", "puuid")
        val cursor:Cursor = db.query(
            TABLE_NAME_RIOT_ACC,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val riotAcc = cursor.getString(cursor.getColumnIndex("riotacc"))
                val puuid = cursor.getString(cursor.getColumnIndex("puuid"))
                res.add(SummonerDB(id, riotAcc, puuid))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res
    }
    fun addOrUpgradeImage(key1DB:String, key2DB:String, imageDB:String, versionDB: String){
        val versionD = getVersionImage(key1DB,key2DB)
        if(versionD == ""){
            val values = ContentValues()
            values.put(key1, key1DB)
            values.put(key2, key2DB)
            values.put(image, imageDB)
            values.put(version, versionDB)
            val db = this.writableDatabase
            db.insert(TABLE_NAME_IMAGE, null, values)
        } else if(versionD != versionDB) {
            val db = this.writableDatabase
            val values = ContentValues()
            values.put(image, imageDB)
            values.put(version, versionDB)
            val selection = "$key1 = ? AND $key2 = ?"
            val selectionArgs = arrayOf(key1DB, key2DB)
            db.update(TABLE_NAME_IMAGE, values, selection, selectionArgs)
        }
    }
    fun getVersionImages():String{
        val db = this.readableDatabase
        val projection = arrayOf("id", version)
        var res = ""
        val cursor:Cursor = db.query(
            TABLE_NAME_IMAGE,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        if(cursor.moveToFirst()){
            res = cursor.getString(1)
        }
        cursor.close()
        return res
    }
    fun getVersionImage(key1DB:String, key2DB: String):String{
        val db = this.readableDatabase
        val projection = arrayOf("id", version)
        var res = ""
        val selection = "key1 = ? AND key2 = ?"
        val selectionArgs = arrayOf(key1DB, key2DB)
        val cursor:Cursor = db.query(
            TABLE_NAME_IMAGE,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if(cursor.moveToFirst()){
            res = cursor.getString(1)
        }
        cursor.close()
        return res
    }
    fun getImages(context: Context): HashMap<String, HashMap<String, Drawable>>{
        val db = this.readableDatabase
        val res: HashMap<String, HashMap<String, Drawable>> = HashMap()
        val projection = arrayOf(key1, key2, image)
        val cursor:Cursor = db.query(
            TABLE_NAME_IMAGE,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val key1 = cursor.getString(0)
                val key2 = cursor.getString(1)
                val image = cursor.getString(2)
                res[key1] = HashMap()
                res[key1]?.put(key2, Cache.base64ToDrawable(image, context))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res
    }

    fun getImage(key1DB: String, key2DB: String): String? {
        val db = this.readableDatabase
        var res: String? = null
        val projection = arrayOf("id", image)
        val selection = "key1 = ? AND key2 = ?"
        val selectionArgs = arrayOf(key1DB, key2DB)
        val cursor:Cursor = db.query(
            TABLE_NAME_IMAGE,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        )
        if (cursor.moveToFirst()) {
            res = cursor.getString(1)
        }
        cursor.close()
        return res
    }

    companion object{
        private const val DATABASE_NAME = "LeagueMastery"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME_RIOT_ACC = "summoner"
        const val id = "id"
        const val riotAcc = "riotacc"
        const val puuid = "puuid"

        const val TABLE_NAME_IMAGE = "image"
        const val key1 = "key1"
        const val key2 = "key2"
        const val version = "version"
        const val image = "image"

        const val TABLE_NAME_FAV_ACC = "favoris"
        const val uid = "uid"

    }
}
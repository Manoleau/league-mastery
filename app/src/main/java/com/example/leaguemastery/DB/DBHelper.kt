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
        db.execSQL(query)
        db.execSQL(query2)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_RIOT_ACC)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_IMAGE)
        onCreate(db)
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
        val projection = arrayOf("id", key1, key2, image)
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
                val id = cursor.getInt(0)
                val key1 = cursor.getString(1)
                val key2 = cursor.getString(2)
                val image = cursor.getString(3)
                res[key1] = HashMap()
                res[key1]?.put(key2, Cache.base64ToDrawable(image, context))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res
    }

    fun getImage(context: Context, key1DB:String, key2DB: String): Drawable? {
        val db = this.readableDatabase
        var res: Drawable? = null
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
            res = Cache.base64ToDrawable(cursor.getString(1), context)
        }
        cursor.close()
        return res
    }

    companion object{
        private val DATABASE_NAME = "LeagueMastery"
        private val DATABASE_VERSION = 1
        val TABLE_NAME_RIOT_ACC = "summoner"
        val id = "id"
        val riotAcc = "riotacc"
        val puuid = "puuid"

        val TABLE_NAME_IMAGE = "image"
        val key1 = "key1"
        val key2 = "key2"
        val version = "version"
        val image = "image"
    }
}
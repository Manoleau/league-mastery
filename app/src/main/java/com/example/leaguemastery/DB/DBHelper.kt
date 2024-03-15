package com.example.leaguemastery.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val query = ("CREATE TABLE " + TABLE_NAME + " ("
                + id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                riotAcc + " VARCHAR(255) NOT NULL," +
                ");")
        db.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    fun addSummoner(riotAccDB : String){
        val values = ContentValues()
        values.put(riotAcc, riotAccDB)
        val db = this.writableDatabase
        db.insert(TABLE_NAME, null, values)
    }
    fun removeSummoner(idDB : Int):Int{
        val db = this.writableDatabase

        val selection = "id = ?"
        val selectionArgs = arrayOf(idDB.toString())

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    @SuppressLint("Range")
    fun getAllSummoner(): ArrayList<SummonerDB> {
        val db = this.readableDatabase
        val res = ArrayList<SummonerDB>()
        val projection = arrayOf("id", "riotacc")
        val cursor:Cursor = db.query(
            TABLE_NAME,
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
                res.add(SummonerDB(id, riotAcc))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return res
    }

    companion object{
        private val DATABASE_NAME = "LeagueMastery"
        private val DATABASE_VERSION = 1
        val TABLE_NAME = "summoner"
        val id = "id"
        val riotAcc = "riotacc"
    }
}
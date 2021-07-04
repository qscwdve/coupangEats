package com.example.coupangeats.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.coupangeats.src.searchDetail.model.ResentSearchDate
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat")
class ResentSearchDatabase(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    private val mTableName = "resentSearchTable"
    private val mID = "ID"
    private val mKeyword = "KEYWORD"
    private var mDate = "RECORDED"

    var mFormat: SimpleDateFormat = SimpleDateFormat("yyyy.MM.dd hh:mm:ss")
    override fun onCreate(db: SQLiteDatabase?) {
        val sql : String =
            "CREATE TABLE if not exists $mTableName( $mID INTEGER PRIMARY KEY AUTOINCREMENT, $mKeyword TEXT, $mDate TEXT);"

        db?.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql : String = "drop table if exists $mTableName"
        db?.execSQL(sql)
        onCreate(db)
    }

    fun addKeyword(db: SQLiteDatabase, keyword: String){

        val cv = ContentValues()
        cv.put(mKeyword, keyword)
        cv.put(mDate, getTime())
        if(db.insert(mTableName, null, cv).toInt() == -1){
            Log.d("database", "insert fail")
        } else Log.d("database", "insert success")
        cv.clear()
    }


    fun deleteTotal(db: SQLiteDatabase){
        db.execSQL("DELETE FROM $mTableName")
    }

    fun deleteToId(db: SQLiteDatabase, id:Int){
        db.execSQL("DELETE FROM $mTableName WHERE $mID=$id")
    }

    fun modifyDate(db: SQLiteDatabase, id:Int){
        val sql = "UPDATE $mTableName SET $mDate='${getTime()}' WHERE $mID=$id"
        db.execSQL(sql)
    }

    fun getResentDate(db: SQLiteDatabase) : ArrayList<ResentSearchDate>{
        val array = ArrayList<ResentSearchDate>()
        val query = "SELECT * FROM $mTableName ORDER BY $mDate DESC;"
        val c = db.rawQuery(query,null)
        while(c.moveToNext()) {
            array.add(ResentSearchDate(c.getInt(0), c.getString(1), (c.getString(2)).substring(5, 10)))
        }
        c.close()
        return array
    }


    private fun getTime(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        return mFormat.format(date)
    }

}
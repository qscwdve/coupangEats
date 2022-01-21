package com.example.coupangeats.util

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.coupangeats.src.cart.model.CartMenuInfo
import com.example.coupangeats.src.main.order.model.OrderMenu

class CartMenuDatabase(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version)  {
    private val mTableName = "cartMenuTable"
    private val mMenuName = "MENUNANE"
    private val mMenuSide = "MENUSIDE"
    private val mPrice = "PRICE"
    private val mNum = "NUM"
    private val mID = "ID"
    private val mMenuIdx = "MENUIDX"

    override fun onCreate(db: SQLiteDatabase?) {
        val sql : String =
            "CREATE TABLE if not exists $mTableName( $mID INTEGER PRIMARY KEY AUTOINCREMENT, $mMenuName TEXT, $mMenuSide TEXT, $mPrice INTEGER, $mNum INTEGER, $mMenuIdx INTEGER);"

        db?.execSQL(sql)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val sql : String = "drop table if exists $mTableName"
        db?.execSQL(sql)
        onCreate(db)
    }

    // 메뉴 저장
    fun menuInsert(db: SQLiteDatabase, menu: CartMenuInfo) {
        val cv = ContentValues()

        cv.put(mMenuName, menu.name)
        cv.put(mMenuSide, menu.sub ?: "")
        cv.put(mPrice, menu.price)
        cv.put(mNum, menu.num)
        cv.put(mMenuIdx, menu.menuIdx)
        if(db.insert(mTableName, null, cv).toInt() == -1){
            Log.d("database", "insert fail")
        } else Log.d("database", "insert success")
    }

    // 메뉴 꺼내기
    fun menuSelect(db: SQLiteDatabase) : ArrayList<CartMenuInfo> {
        val array = ArrayList<CartMenuInfo>()

        val query = "SELECT * FROM $mTableName;"
        val c = db.rawQuery(query,null)
        while(c.moveToNext()) {
            array.add(CartMenuInfo(c.getInt(0),c.getString(c.getColumnIndex(mMenuName)), c.getInt(c.getColumnIndex(mNum)), c.getInt(c.getColumnIndex(mPrice)), c.getString(c.getColumnIndex(mMenuSide)), c.getInt(c.getColumnIndex(mMenuIdx))))
        }
        c.close()
        return array
    }

    // 메뉴 꺼내기
    fun menuSelect2(db: SQLiteDatabase) : ArrayList<OrderMenu>{
        val array = ArrayList<OrderMenu>()

        val query = "SELECT * FROM $mTableName;"
        val c = db.rawQuery(query,null)
        while(c.moveToNext()) {
            array.add(OrderMenu(c.getInt(c.getColumnIndex(mNum)), c.getString(c.getColumnIndex(mMenuName)), c.getString(c.getColumnIndex(mMenuSide)), c.getInt(c.getColumnIndex(mPrice)).toString()))
        }
        c.close()
        return array
    }

    // 총 결제 금액
    fun menuTotalPrice(db: SQLiteDatabase) : Int {
        var totalPrice = 0

        val query = "SELECT * FROM $mTableName;"
        val c = db.rawQuery(query,null)
        while(c.moveToNext()) {
            totalPrice += c.getInt(c.getColumnIndex(mPrice)) * c.getInt(c.getColumnIndex(mNum))
        }
        c.close()

        return totalPrice
    }

    // 컬림 개수 확인
    fun checkMenuNum(db: SQLiteDatabase) : Int {
        var num = 0

        val query = "SELECT COUNT(*) FROM $mTableName;"
        val c = db.rawQuery(query,null)
        while(c.moveToNext()) {
            num = c.getInt(0)
        }
        c.close()
        return num
    }

    // 메뉴 전체 삭제
    fun deleteTotal(db: SQLiteDatabase) {
        db.execSQL("DELETE FROM $mTableName")
    }

    // 메뉴 삭제
    fun deleteId(db: SQLiteDatabase, id:Int) {
        db.execSQL("DELETE FROM $mTableName WHERE $mID=$id")
    }

    // 메뉴 수정
    fun menuIdModify(db: SQLiteDatabase, id: Int, num: Int) {
        val sql = "UPDATE $mTableName SET $mNum=$num WHERE $mID=$id"
        db.execSQL(sql)
    }
}
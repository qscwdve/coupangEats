package com.example.coupangeats.util

import android.annotation.SuppressLint
import android.os.health.SystemHealthManager
import android.provider.ContactsContract
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun main(){
    val now = System.currentTimeMillis()
    val data = Date(now)
    val simpleDateFormat = SimpleDateFormat("yyMMddhhmmss")
    val getTime = simpleDateFormat.format(data)
    print("현재시간 : ${now.toString()}${getTime.toString()}")
}
fun makeFileName(): String {
    val now = System.currentTimeMillis()
    val data = Date(now)
    val simpleDateFormat = SimpleDateFormat("yyMMddhhmmss")
    val getTime = simpleDateFormat.format(data)
    return "${now.toString()}${getTime.toString()}"
}
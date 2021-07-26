package com.example.coupangeats.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.RemoteAction
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.coupangeats.R
import com.example.coupangeats.src.deliveryStatus.DeliveryStatusActivity
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
class CustomNotification(val context: Context) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    companion object {
        const val PRIMARY_CHANNEL_ID = "primary_notification_channel"
    }

    fun createAlarmNotification(id:Int){
        createNotificationChannel(id)
    }

    private fun deliverNotification(id:Int) {
        val contentIntent = Intent(context, DeliveryStatusActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            id,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val customLayout = RemoteViews(context.packageName, R.layout.notification_delivery_complete)
        val builder =
            NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_e)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(contentPendingIntent)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        customLayout.setTextViewText(R.id.notification_delivery_time, nowTime())
        builder.setCustomContentView(customLayout)
        notificationManager.notify(id, builder.build())
    }

    private fun createNotificationChannel(id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                "Stand up notification",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "AlarmManager Tests"
            notificationManager.createNotificationChannel(notificationChannel)
        }
        deliverNotification(id)
    }

    fun deleteNotificationId(id:Int){
        notificationManager.cancel(id)
    }


    fun nowTime() : String{

        val sdf = SimpleDateFormat("hh:mm")
        val time = sdf.format(Date(System.currentTimeMillis()))
        val hour = (time[0].toString() + time[1].toString()).toInt()
        val am_pm = if(hour > 12) "오후" else "오전"

        return "$am_pm $time"
    }
}
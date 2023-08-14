package com.digitaldream.linkskool.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

const val CHANNEL_ID = "0"
class NotifyUtil: Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Receipt",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Payment Receipt"

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
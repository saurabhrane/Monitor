package me.saurabhrane.monitor

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import me.saurabhrane.monitor.helper.Constants
import me.saurabhrane.monitor.helper.SystemUtils

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        SystemUtils.launchMonitorService(applicationContext)
        SystemUtils.enqueueSystemWorkers()

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Constants.CHANNEL_ID,
                "Monitor Service Active",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }


}
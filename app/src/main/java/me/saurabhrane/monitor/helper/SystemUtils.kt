package me.saurabhrane.monitor.helper

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import me.saurabhrane.monitor.services.MonitorService
import me.saurabhrane.monitor.workers.SystemCheckWorker
import java.util.concurrent.TimeUnit


object SystemUtils {

    private fun isServiceRunning(context: Context, serviceName: String): Boolean {
        var serviceRunning = false
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.getRunningServices(50)
        val i: Iterator<ActivityManager.RunningServiceInfo> = l.iterator()
        while (i.hasNext()) {
            val runningServiceInfo = i.next()
            if (runningServiceInfo.service.className == serviceName) {
                serviceRunning = true
                if (runningServiceInfo.foreground) {
                    //service run in foreground
                }
            }
        }
        return serviceRunning
    }

    fun enqueueSystemWorkers() {
        val systemCheckWorkerBuilder = PeriodicWorkRequest.Builder(
            SystemCheckWorker::class.java, 15, TimeUnit.MINUTES
        )
        val request = systemCheckWorkerBuilder.build()
        WorkManager.getInstance()
            .enqueueUniquePeriodicWork("system", ExistingPeriodicWorkPolicy.KEEP, request)
    }

    fun launchMonitorService(context: Context) {
        if (!isServiceRunning(context, "MonitorService")) {
            val monitorServiceIntent = Intent(context, MonitorService::class.java)
            monitorServiceIntent.putExtra("inputExtra", Constants.MONITOR_SERVICE_DESCRIPTION)
            ContextCompat.startForegroundService(context, monitorServiceIntent)
        }
    }

}
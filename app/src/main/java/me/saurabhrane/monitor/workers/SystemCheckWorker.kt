package me.saurabhrane.monitor.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import me.saurabhrane.monitor.helper.SystemUtils

class SystemCheckWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        SystemUtils.launchMonitorService(applicationContext)
        return Result.success()
    }


}
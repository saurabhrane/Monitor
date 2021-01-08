package me.saurabhrane.monitor.services

import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import me.saurabhrane.monitor.R
import me.saurabhrane.monitor.helper.Constants
import me.saurabhrane.monitor.helper.Constants.CHANNEL_ID
import me.saurabhrane.monitor.helper.Constants.TIMER_DELAY
import me.saurabhrane.monitor.helper.Constants.TIMER_INTERVAL
import me.saurabhrane.monitor.helper.NotificationHelper
import me.saurabhrane.monitor.receivers.BluetoothBroadcastReceiver
import me.saurabhrane.monitor.ui.MainActivity
import java.util.*
import kotlin.concurrent.timerTask

class MonitorService : Service() {

    private var timer: Timer? = null
    private lateinit var timerTask: TimerTask
    private var counter = 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val bluetoothBroadcastReceiver = BluetoothBroadcastReceiver()
        registerReceiver(
            bluetoothBroadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )

        val input = intent!!.getStringExtra("inputExtra")
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notification = NotificationCompat.Builder(application, CHANNEL_ID)
            .setContentTitle("Monitor is running")
            .setContentText("Network : Off | Bluetooth : ${getBtState()}")
            .setSmallIcon(R.drawable.ic_android)
            .setContentIntent(pendingIntent)
            .build()

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        cm.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val activeNetwork = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(activeNetwork)!!
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        NotificationHelper.displayNotification(
                            this@MonitorService,
                            "Monitor is running",
                            "Network : On (Mobile Data) | Bluetooth : ${getBtState()}"
                        )
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        NotificationHelper.displayNotification(
                            this@MonitorService,
                            "Monitor is running",
                            "Network : On (WIFI) | Bluetooth : ${getBtState()}"
                        )
                    }
                    else -> {
                        NotificationHelper.displayNotification(
                            this@MonitorService,
                            "Monitor is running",
                            "Network : On | Bluetooth : ${getBtState()}"
                        )
                    }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.e("error", "onLost")
                NotificationHelper.displayNotification(
                    this@MonitorService,
                    "Monitor is running",
                    "Network : Off | Bluetooth : ${getBtState()}"
                )

            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        NotificationHelper.displayNotification(
                            this@MonitorService,
                            "Monitor is running",
                            "Network : On (Mobile Data) | Bluetooth : ${getBtState()}"
                        )
                    }
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        NotificationHelper.displayNotification(
                            this@MonitorService,
                            "Monitor is running",
                            "Network : On (WIFI) | Bluetooth : ${getBtState()}"
                        )
                    }
                }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                Log.e("error", "onUnavailable")
                NotificationHelper.displayNotification(
                    this@MonitorService,
                    "Monitor is running",
                    "Network : Off | Bluetooth : ${getBtState()}"
                )
            }

        })

        startForeground(1, notification)

        startTimer()

        return START_STICKY

    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun startTimer() {
        timer = Timer()
        initializeTimerTask()
        timer?.schedule(timerTask, TIMER_DELAY, TIMER_INTERVAL)
    }

    private fun initializeTimerTask() {
        timerTask = timerTask {
            run {
                Log.i(Constants.TAG, "Service timer " + counter++)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }

    private fun stopTimer() {
        timer?.let {
            it.cancel()
            timer = null
        }
    }

    private fun getBtState(): String {
        val btAdapter = BluetoothAdapter.getDefaultAdapter()
        return if (btAdapter.isEnabled) "On" else "Off"
    }


}

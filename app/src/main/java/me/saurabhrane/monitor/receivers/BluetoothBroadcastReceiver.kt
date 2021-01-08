package me.saurabhrane.monitor.receivers

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import me.saurabhrane.monitor.helper.NotificationHelper

class BluetoothBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        var action = intent.action
        when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
            BluetoothAdapter.STATE_OFF -> {
                showNotificationWithNetworkStatus(context, "Off")
            }
            BluetoothAdapter.STATE_ON -> {
                showNotificationWithNetworkStatus(context, "On")
            }
        }

    }

    private fun showNotificationWithNetworkStatus(context: Context, state: String) {

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        NotificationHelper.displayNotification(
            context,
            "Monitor is running",
            "Network : Off | Bluetooth : $state"
        )

        cm.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                val activeNetwork = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(activeNetwork)!!
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    NotificationHelper.displayNotification(
                        context,
                        "Monitor is running",
                        "Network : On (Mobile Data) | Bluetooth : $state"
                    )
                } else {
                    NotificationHelper.displayNotification(
                        context,
                        "Monitor is running",
                        "Network : On (WIFI) | Bluetooth : $state"
                    )
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                Log.e("error", "onLost")
                NotificationHelper.displayNotification(
                    context,
                    "Monitor is running",
                    "Network : Off | Bluetooth : $state"
                )
            }

            override fun onUnavailable() {
                super.onUnavailable()
                Log.e("error", "onUnavailable")
                NotificationHelper.displayNotification(
                    context,
                    "Monitor is running",
                    "Network : Off | Bluetooth : $state"
                )
            }

        })



    }


}

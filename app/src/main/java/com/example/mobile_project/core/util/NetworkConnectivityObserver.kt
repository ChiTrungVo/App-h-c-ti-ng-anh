package com.example.mobile_project.core.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

enum class ConnectivityStatus {
    Available, Unavailable
}

class NetworkConnectivityObserver(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val status: Flow<ConnectivityStatus> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                launch { send(ConnectivityStatus.Available) }
            }

            override fun onLosing(network: Network, maxMsToLive: Int) {
                super.onLosing(network, maxMsToLive)
                launch { send(ConnectivityStatus.Unavailable) }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                launch { send(ConnectivityStatus.Unavailable) }
            }

            override fun onUnavailable() {
                super.onUnavailable()
                launch { send(ConnectivityStatus.Unavailable) }
            }
        }

        connectivityManager.registerDefaultNetworkCallback(callback)

        val currentState = getCurrentConnectivityStatus(connectivityManager)
        send(currentState)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()

    private fun getCurrentConnectivityStatus(connectivityManager: ConnectivityManager): ConnectivityStatus {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        // Note: Sometimes VALIDATED is not immediately true. For simpler checks:
        val isConnected = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        return if (isConnected) ConnectivityStatus.Available else ConnectivityStatus.Unavailable
    }
}

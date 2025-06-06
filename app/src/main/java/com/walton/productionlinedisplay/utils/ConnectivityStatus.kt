package com.walton.productionlinedisplay.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import com.walton.productionlinedisplay.R

class ConnectivityStatus(context: Context) : LiveData<AlertDialogStatus<String>>() {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
 
    private val networkCallbacks = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(AlertDialogStatus.Connected("Connected"))
        }
 
        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(AlertDialogStatus.ConnectionLost(context.getString(R.string.no_internet_connection_message)))
        }
 
        override fun onUnavailable() {
            super.onUnavailable()
            postValue(AlertDialogStatus.ConnectionNotAvailable(context.getString(R.string.no_internet_connection_message)))
        }
    }
 
    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkInternet(){
        // we can user activeNetwork because our min sdk version is 23 if our min sdk version is less than 23
        // then we have to user connectivityManager.activeNetworkInfo (Note: Deperated)
 
        val network = connectivityManager.activeNetwork
        if(network==null){
            postValue(AlertDialogStatus.ConnectionNotAvailable("Please check your internet connection"))
        }
 
        /**
         * After checking network its time to check network internet capabilities
         * whether connection has internet or not for that we will register the network
         * and then check network capabilities with the help of the callbacks
         */
        val requestBuilder =NetworkRequest.Builder().apply {
            addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) // also for sdk version 23 or above
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        }.build()
 
        connectivityManager.registerNetworkCallback(requestBuilder,networkCallbacks)
    }
 
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActive() {
        super.onActive()
        checkInternet()
    }
 
    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkCallbacks)
    }
}
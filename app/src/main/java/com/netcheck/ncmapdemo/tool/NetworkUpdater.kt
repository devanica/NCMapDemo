package com.netcheck.ncmapdemo.tool

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.MutableLiveData

/**
 * This class will enable us to monitor connectivity change and if internet connection is available.
 *
 * It only has to be enabled in the class where it is used, in our case we did it in ViewPagesAdapter
 * and passed it to fragments upon their creation.
 *
 * Once that is done trough LiveData's observable we are getting connection on(true) or off(false).
 *
 * It checks both wireless and mobile internet.
 * As explained here: https://stackoverflow.com/questions/46766790/
 *                                          how-define-broadcastreceiver-with-connectivity-change
 *
 *******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic </dd>
 * <dt><b> Created:   </b></dt><dd>  12.01.2020. </dd>
 * </dl>
 ******************************************************************************
 *
 */
object NetworkUpdater: NetworkCallback() {
    var isNetworkDetected: MutableLiveData<Boolean> = MutableLiveData()

    private val networkRequest: NetworkRequest = NetworkRequest.Builder()
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .build()

    fun listenToConnectivityChange(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        isNetworkDetected.postValue(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        isNetworkDetected.postValue(false)
    }

}
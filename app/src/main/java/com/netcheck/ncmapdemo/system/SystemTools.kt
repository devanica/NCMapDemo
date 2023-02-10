package com.netcheck.ncmapdemo.system

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import androidx.core.app.ActivityCompat
import com.netcheck.ncmapdemo.Constants
import com.netcheck.ncmapdemo.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * This class provides some basic functions.
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck  </dd>
 * <dt><b> Author:    </b></dt><dd>  Peter Hahne   </dd>
 * <dt><b> Created:   </b></dt><dd>  05.02.2022. </dd>
 * </dl>
 ******************************************************************************
 *
 * */
object SystemTools {
    private const val B: Long = 1
    private const val KB = B * 1024
    private const val MB = KB * 1024
    private const val GB = MB * 1024

    fun getConnManager(context: Context): ConnectivityManager {
        return context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    fun getLocManager(context: Context): LocationManager {
        return context.applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    fun getTelManager(context: Context): TelephonyManager {
        return context.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    fun getWifiManager(context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    /**
     * Get and format current date/time.
     *
     * */
    fun currentDateTime(): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun currentTime(): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

    /**
     * Get signal level of used connection.
     * @param context;
     *
     */
    fun getSignalLevel(context: Context): String {
        var level = context.resources.getString(R.string.unknown)
        val connection = findNetwork(context)
        if (connection == Constants.wifi) {
            val wifiManager =
                context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val linkSpeed = wifiManager.connectionInfo.rssi
            level = linkSpeed.toString()
        }
        return level
    }

    /**
     * Find, if device connected, which network it is connected to.
     * @param context;
     *
     * */
    fun findNetwork(context: Context): String {
        var internet = context.resources.getString(R.string.disconnected)
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connManager.allNetworks.isNotEmpty()) {
            internet =  if (!connManager.isActiveNetworkMetered) {
                Constants.wifi
            } else {
                Constants.mobile
            }
        } else {
            context.resources.getString(R.string.disconnected)
        }
        return internet
    }

    /** Returns the current network technology.
     * @param context Context;
     * @return technology enumerator;
     */
    @SuppressLint("MissingPermission")
    fun nwTechnology(context: Context): SystemDefsI.EnNetwTechn {
        var nt = 0
        val telephonyManager = getTelManager(context)
        if ((ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                nt = telephonyManager.dataNetworkType

        val nt1: SystemDefsI.EnNetwTechn = SystemDefsI.EnNetwTechn.values()[nt]
        val nt2: SystemDefsI.EnNetwTechn = SystemDefsI.EnNetwTechn.values()[getMobileNetworkTechnology(context)]
        return if (nt == 0) nt1 else nt2
    }

    private fun getMobileNetworkTechnology(context: Context): Int {
        val conn = getConnManager(context)
        var nt = 0
        for (nw in conn.allNetworks) {
            val info = conn.getNetworkInfo(nw)
            if (info!!.type == ConnectivityManager.TYPE_MOBILE) {
                nt = info.subtype
            }
        }
        return nt
    }

    fun getDataConnectionType(context: Context): SystemDefsI.EnNetwType {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return SystemDefsI.EnNetwType.UNKNOWN
        val conn: ConnectivityManager = getConnManager(context)
        val network = conn.activeNetwork ?: return SystemDefsI.EnNetwType.UNKNOWN
        val networkCapabilities = conn.getNetworkCapabilities(network)
        if (networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        ) {
            if (networkCapabilities.hasTransport(SystemDefsI.EnNetwType.CELLULAR.ordinal)) return SystemDefsI.EnNetwType.CELLULAR
            if (networkCapabilities.hasTransport(SystemDefsI.EnNetwType.WIFI.ordinal)) return SystemDefsI.EnNetwType.WIFI
            if (networkCapabilities.hasTransport(SystemDefsI.EnNetwType.BLUETOOTH.ordinal)) return SystemDefsI.EnNetwType.BLUETOOTH
            if (networkCapabilities.hasTransport(SystemDefsI.EnNetwType.ETHERNET.ordinal)) return SystemDefsI.EnNetwType.ETHERNET
            if (networkCapabilities.hasTransport(SystemDefsI.EnNetwType.VPN.ordinal)) return SystemDefsI.EnNetwType.VPN
            if (networkCapabilities.hasTransport(SystemDefsI.EnNetwType.WIFI_AWARE.ordinal)) return SystemDefsI.EnNetwType.WIFI_AWARE
            if (networkCapabilities.hasTransport(SystemDefsI.EnNetwType.LOWPAN.ordinal)) return SystemDefsI.EnNetwType.LOWPAN
        }
        return SystemDefsI.EnNetwType.UNKNOWN
    }

    /**
     * Method to calculate traffic in bits per second (returns real value).
     * @param bits to calculate;
     * @return - calculation result in string;
     *
     */
    fun getTrafficInBitsPerSecond(bits: Double): String {
        if (bits < KB) return String.format(Locale.US, "%.1f", bits)
        if (bits < MB) return String.format(Locale.US, "%.1f", bits / 1000)
        return if (bits < GB)
            String.format(Locale.US, "%.1f", bits / (1000 * 1000)
        ) else String.format(
            Locale.US, "%.1f", bits / (1000 * 1000 * 1000)
        )
    }

    fun getTrafficInKBitsPerSecond(bits: Double): String {
        return String.format(Locale.US, "%.1f", bits / 1000)
    }

    /**
     * Method to calculate traffic in bits per second (returns real number plus unit).
     * @param bits to calculate;
     * @return calculated result;
     *
     */
    fun transformBitsToFormat(bits: Double): String {
        if (bits < KB) return String.format(
            Locale.US, "%.1f", bits) + "bit/s"
        if (bits < MB) return String.format(
            Locale.US, "%.1f", bits / 1000) + "Kbit/s"
        return if (bits < GB) String.format(
            Locale.US, "%.1f", bits / (1000 * 1000)) + "Mbit/s"
        else String.format(
            Locale.US, "%.1f", bits / (1000 * 1000 * 1000)) + "Gbit/s"
    }

    fun transformBitsToFormatBrowsing(bits: Double): String {
        if (bits < KB) return String.format(
            Locale.US, "%.1f", bits) + "bit"
        if (bits < MB) return String.format(
            Locale.US, "%.1f", bits / 1000) + "Kbit"
        return if (bits < GB) String.format(
            Locale.US, "%.1f", bits / (1000 * 1000)) + "Mbit"
        else String.format(
            Locale.US, "%.1f", bits / (1000 * 1000 * 1000)) + "Gbit"
    }

    /**
     * Method to calculate traffic in bits per second (returns only unit).
     * @param bits to calculate;
     * @return calculated result;
     *
     */
    fun getTrafficUnitFromBitsPerSecond(bits: Double): String {
        if (bits < KB) return "bit/s"
        if (bits < MB) return "Kbit/s"
        return if (bits < GB) "Mbit/s" else "Gbit/s"
    }

    /**
     * In order to track bytes downloaded from a page we should use getUidRxBytes(UID) method. This
     * method is used because it will present only the data downloaded in our app. It is used only
     * in browsing test so far, but it can probably be used in YouTube test as well.
     *
     * It is explained here:
     * https://stackoverflow.com/questions/34217754/getuidrxbytes-and-getuidtxbytes-is-
     * inaccurate-in-android-6-0-or-sometimes-when-i
     *
     */
    fun getNetCheckUID(activity: Activity): Int {
        val packages = activity.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            if (packageInfo.packageName == "com.netcheck.ncmapdemo") {
                return packageInfo.uid
            }
        }
        return -1
    }

}
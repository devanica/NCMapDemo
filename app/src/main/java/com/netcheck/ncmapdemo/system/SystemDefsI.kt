package com.netcheck.ncmapdemo.system

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.netcheck.ncmapdemo.R

/**
 * The interface provides a number common of constants.
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
interface SystemDefsI {

    enum class EnTestType(var testName: String,
                          var jsonAvg: String,
                          var jsonBest: String,
                          var pid: Int,
                          var unit_limit: Int,
                          var unit_small: Int,
                          var unit_big: Int,
                          var excellent: Int,
                          var verygood: Int,
                          var good: Int,
                          var fair: Int,
                          var invert: Boolean) {
        UPLOAD("HTTP_UL", "UL_RATE_AVG", "UL_RATE_BEST",1, 1000, R.string.traffic_kbits, R.string.traffic_mbits, 21200, 10000, 2000, 1000, false),
        DOWNLOAD("HTTP_DL", "DL_RATE_AVG", "DL_RATE_BEST", 2, 1000, R.string.traffic_kbits, R.string.traffic_mbits, 6000, 3000, 600, 300, false),
        PING("PING", "LATENCY_AVG", "LATENCY_BEST",3, 10000, R.string.ping_ms, R.string.ping_s, -65, -150, -100, -5000, true),
        BROWSING("BROWSING", "RESULT_AVG", "RESULT_BEST",4, -1, -1, -1, -1, -1, -1, -1, false),
        YOUTUBE("YOUTUBE", "RESULT_AVG", "RESULT_BEST",5, -1, -1, -1, -1, -1, -1, -1, false);

        fun getPID(): Int {
            return pid
        }

        fun getUnitSmall(context: Context): String {
            return context.getString(unit_small)
        }

        fun getUnitBig(context: Context): String {
            return context.getString(unit_big)
        }

        fun getCategory(initValue: Double, context: Context): Int {
            var value = initValue
            if (value <= 0) {
                return ContextCompat.getColor(context, R.color.black)
            }
            if (invert) value *= -1
            if (value >= excellent) {
                return ContextCompat.getColor(context, R.color.transparent_blue_eighty)
            }
            if (value >= verygood) {
                return ContextCompat.getColor(context, R.color.transparent_blue_seventy)
            }
            if (value >= good) {
                return ContextCompat.getColor(context, R.color.transparent_blue_fifty)
            }
            return if (value >= fair) {
                ContextCompat.getColor(context, R.color.transparent_blue_thirty)
            } else {
                ContextCompat.getColor(context, R.color.transparent_red)
            }
        }

        fun getLimit(): Int {
            return unit_limit
        }
    }

    enum class EnSimState(private val state: Int) {
        UNKNOWN(TelephonyManager.SIM_STATE_UNKNOWN),
        ABSENT(TelephonyManager.SIM_STATE_ABSENT),
        PIN_REQUIRED(TelephonyManager.SIM_STATE_PIN_REQUIRED),
        PUK_REQUIRED(TelephonyManager.SIM_STATE_PUK_REQUIRED),
        NETWORK_LOCKED(TelephonyManager.SIM_STATE_NETWORK_LOCKED),
        READY(TelephonyManager.SIM_STATE_READY),
        @RequiresApi(Build.VERSION_CODES.O)
        NOT_READY(TelephonyManager.SIM_STATE_NOT_READY),
        @RequiresApi(Build.VERSION_CODES.O)
        PERM_DISABLED(TelephonyManager.SIM_STATE_PERM_DISABLED),
        @RequiresApi(Build.VERSION_CODES.O)
        CARD_IO_ERROR(TelephonyManager.SIM_STATE_CARD_IO_ERROR),
        @RequiresApi(Build.VERSION_CODES.O)
        CARD_RESTRICTED(TelephonyManager.SIM_STATE_CARD_RESTRICTED);

        fun getState(): Int {
            return state
        }
    }

    enum class EnNetworkType(private val type: Int) {
        UNKNOWN(TelephonyManager.NETWORK_TYPE_UNKNOWN),
        GPRS(TelephonyManager.NETWORK_TYPE_GPRS),
        EDGE(TelephonyManager.NETWORK_TYPE_EDGE),
        UMTS(TelephonyManager.NETWORK_TYPE_UMTS),
        CDMA(TelephonyManager.NETWORK_TYPE_CDMA),
        EVDO_0(TelephonyManager.NETWORK_TYPE_EVDO_0),
        EVDO_A(TelephonyManager.NETWORK_TYPE_EVDO_A),
        ONExRTT(TelephonyManager.NETWORK_TYPE_1xRTT),
        HSDPA(TelephonyManager.NETWORK_TYPE_HSDPA),
        HSUPA(TelephonyManager.NETWORK_TYPE_HSUPA),
        HSPA(TelephonyManager.NETWORK_TYPE_HSPA),
        IDEN(TelephonyManager.NETWORK_TYPE_IDEN),
        EVDO_B(TelephonyManager.NETWORK_TYPE_EVDO_B),
        LTE(TelephonyManager.NETWORK_TYPE_LTE),
        EHRPD(TelephonyManager.NETWORK_TYPE_EHRPD),
        HSPAP(TelephonyManager.NETWORK_TYPE_HSPAP),
        @RequiresApi(Build.VERSION_CODES.N_MR1)
        GSM(TelephonyManager.NETWORK_TYPE_GSM),
        @RequiresApi(Build.VERSION_CODES.N_MR1)
        SCDMA(TelephonyManager.NETWORK_TYPE_TD_SCDMA),
        @RequiresApi(Build.VERSION_CODES.N_MR1)
        IWLAN(TelephonyManager.NETWORK_TYPE_IWLAN), //18
        @RequiresApi(Build.VERSION_CODES.Q)
        NR(TelephonyManager.NETWORK_TYPE_NR); //20

        fun getType(): Int {
            return type
        }
    }

    /** Enumeration of technologies as defined by `TelephonyManager`.  */
    enum class EnNetwTechn {
        UNKNOWN,
        GPRS,
        EDGE,
        UMTS,
        CDMA,
        EVDO_0,
        EVDO_A,
        RTTx1,
        HSDPA,
        HSUPA,
        HSPA,
        IDEN,
        EVDO_B,
        LTE,
        EHRPD,
        HSPAP,
        GSM,
        TD_SCDMA,
        IWLAN,
        LTE_CA,
        NR;
    }

    enum class EnNetwType {
        CELLULAR,
        WIFI,
        BLUETOOTH,
        ETHERNET,
        VPN,
        WIFI_AWARE,
        LOWPAN,
        UNKNOWN,
        NONE;
    }

}
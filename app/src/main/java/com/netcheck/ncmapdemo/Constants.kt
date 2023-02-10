package com.netcheck.ncmapdemo

/**
 * This class holds most of the constants we need on this project
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck  </dd>
 * <dt><b> Author:    </b></dt><dd>  Peter Hahne </dd>
 * <dt><b> Created:   </b></dt><dd>  04.02.2022. </dd>
 * </dl>
 ******************************************************************************
 *
 */
object Constants {
    var so_at = "START_OF_ALIGN_TOP"
    var so_ab = "START_OF_ALIGN_BOTTOM"
    var eo_at = "END_OF_ALIGN_TOP"
    var cip = "CENTER_IN_PARENT"

    var ae_b = "ALIGN_END_BELLOW"
    var as_b = "ALIGN_START_BELOW"
    var ch_b = "CENTER_HORIZONTAL_BELLOW"
    //
    const val KIBI_BYTE = 1024
    const val MEBIBTYE  = KIBI_BYTE * 1024
    const val GIBIBYTE  = MEBIBTYE * 1024
    const val NULL_DOUBLE = Double.NaN

    // units
    const val BIT_PER_SECOND     = "Bit/s"
    const val KILOBIT_PER_SECOND = "KKit/s"
    const val MEGABIT_PER_SECOND = "MBit/s"
    const val GIGABIT_PER_SECOND = "GBit/s"

    // database
    const val DATABASE_NAME = "testdatabase.db"

    const val SDK_UPLOAD_URL: String = "https://upload.ncmq.messserver.de/upload2_ncapp"

    // upload/download files
    const val DOWNLOAD_FILE_NAME = "1gb.xwd"
    const val UPLOAD_FILE_NAME = "1gb.bin"

    // settings
    const val settings = "SETTINGS"
    const val sdk = "SDK"
    const val filterTestType = "FILTER_TEST"
    const val filterUseBest = "FILTER_BEST_AVG"
    const val filterShowUserTest = "FILTER_SHOW_MY_TEST"
    const val showLocationPermission = "LOCATION_PERMISSION"
    const val showCallPermission = "CALL_PERMISSION"
    const val showBackgroundPermission = "BACKGROUND_PERMISSION"
    const val showSDKPermission = "SDK_PERMISSION"
    const val showTrafficWarning = "TRAFFIC_WARNING"

    // selections
    const val wifi = "Wifi"
    const val mobile = "Mobile"
    const val ping = "Ping"
    const val download = "Download"
    const val upload = "Upload"
    const val browsing = "Browsing"
    const val youtube = "Youtube"
    const val performance = "Performance"

    const val car = "Car"
    const val bus = "Bus"
    const val train = "Train"
    const val outdoors = "Outdoors"
    const val indoors = "Indoors"
    const val other = "Other"

    // KPIs Youtube
    const val loadTime = "Load Time"
    const val accessFailure = "Video access failure"
    const val playoutCutOff = "Video playout cut-off"
    const val playoutDuration = "Video playout duration"
    const val videoQuality = "Video quality"
    const val lagDuration = "Lag duration"
    const val lagCount = "Lag count"

    // KPIs Browsing
    const val loadAmount = "Download size"
    const val loadingTime = "Loading time"
    const val loadRatio = "Loading ratio"
    const val latency = "Latency"

    // video quality
    const val tiny = "144p"
    const val small = "240p"
    const val medium = "360p"
    const val large = "480p"
    const val hd720 = "720p"
    const val hd1080 = "1080p"
    const val hd1440p = "1440p"
    const val hd2160 = "2160p"
    const val hd2880 = "2880p"
    const val highres = "4320p"

    // debug
    const val TAG = "NetCheckTest"

    // notification
    const val notificationChannelId = "NETCHECK"
    const val notificationId = 1

    // map
    const val place = "place"
    const val test = "test"
    const val tile = "tile"
    const val close = "close"
    const val filter = "filter"
    const val tutorialMap = "tutorialMap"
    const val tutorialMain = "tutorialMain"
    const val tutorialTool = "tutorialTool"
    const val unknown = "unknown"
    const val empty = "empty"

    // clicks
    const val card = "card"
    const val map = "map"
    const val remove = "remove"
}

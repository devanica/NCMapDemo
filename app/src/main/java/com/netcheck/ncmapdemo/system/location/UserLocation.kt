package com.netcheck.ncmapdemo.system.location

import android.location.Location
import androidx.lifecycle.MutableLiveData

/**
 * In this object we store location when we get it.
 *
 *******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic </dd>
 * <dt><b> Created:   </b></dt><dd>  09.03.2020. </dd>
 * </dl>
 ******************************************************************************
 *
 */
object UserLocation {
    var locationUpdate: MutableLiveData<Location> = MutableLiveData()
}
package com.netcheck.ncmapdemo.system.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.netcheck.ncmapdemo.system.EnPermissions
import com.netcheck.ncmapdemo.system.location.UserLocation.locationUpdate
import com.netcheck.ncmapdemo.system.permission.PermissionCheckI
import com.netcheck.ncmapdemo.system.permission.PermissionHandler

/**
 * In this object we get current user location using fused location provider.
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
object LocationHandler: PermissionCheckI {
    private var userUserLocation: Location? = null

    fun getUserLocation(): Location? {
        return userUserLocation
    }

    /**
     * Find location using fused location provider. The reason why I also post update here is because
     * if user enables location and clicks on myLocation it will take some time before the location is
     * obtained so to prevent the "buggy" behaviour we take him to location once it is gotten.
     * This is how Google handled this issue.
     * @param context
     *
     */
    @SuppressLint("MissingPermission")
    fun obtainUserLocation(context: Context) {
        if (isGranted(context)) {
            LocationServices.getFusedLocationProviderClient(context).lastLocation.addOnSuccessListener {
                if (it != null) {
                    userUserLocation = it
                    locationUpdate.postValue(it)
                }
            }
        }
    }

    override fun isGranted(context: Context): Boolean {
        var granted = false
        for (i in EnPermissions.values()) {
            if (i.ordinal == 2) break
            granted = PermissionHandler.permissionCheck(
                EnPermissions.values()[i.ordinal].getPermission(),
                context
            )
            if (!granted) {
                break
            }
        }
        return granted
    }
}
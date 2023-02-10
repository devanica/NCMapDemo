package com.netcheck.ncmapdemo.system

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

/**
 * Permission class provides us with enums required to check for permission.
 *
 *******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic </dd>
 * <dt><b> Created:   </b></dt><dd>  09.03.2022. </dd>
 * </dl>
 ******************************************************************************
 *
 */
enum class EnPermissions(private val permission: String) {
    ACCESS_FINE_LOCATION(Manifest.permission.ACCESS_FINE_LOCATION),
    ACCESS_COARSE_LOCATION(Manifest.permission.ACCESS_COARSE_LOCATION),
    @RequiresApi(Build.VERSION_CODES.Q)
    ACCESS_BACKGROUND_LOCATION(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
    READ_PHONE_STATE(Manifest.permission.READ_PHONE_STATE);

    fun getPermission(): String {
        return permission
    }
}
package com.netcheck.ncmapdemo.system.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

/**
 * PermissionHandler object provides us with method that checks if given permission is granted.
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
object PermissionHandler {
    fun permissionCheck(permission: String, context: Context): Boolean {
        return (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED)
    }
}
package com.netcheck.ncmapdemo.system.permission

import android.content.Context

/**
 * PermissionCheckI interface provides us with method to implement where checks are needed.
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
interface PermissionCheckI {
    fun isGranted(context: Context): Boolean
}

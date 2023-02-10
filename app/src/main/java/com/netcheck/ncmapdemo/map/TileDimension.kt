package com.netcheck.ncmapdemo.map

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

/**
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd> NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd> Peter Hahne </dd>
 * <dt><b> Created:   </b></dt><dd> unknown </dd>
 * </dl>
 ******************************************************************************
 *
 */
data class TileDimension(val bounds: LatLngBounds,
                         val extBounds: LatLngBounds,
                         val dimensionX: Int,
                         val dimensionY: Int,
                         val dimenX: Double,
                         val dimenY: Double,
                         val latLng: LatLng,
                         val zoom: Int)
package com.netcheck.ncmapdemo.map

import com.google.android.gms.maps.model.LatLng

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
data class Tiles(
    var pos: LatLng,
    var avg: Double,
    var best: Double,
    var mapData: Map<Int, TileData> = mutableMapOf()
) {
    var key: Long = MapHelper.calculateKey(pos)


    fun getValues(): Map<Int, TileData> {
        return mapData
    }

    fun isEmpty(): Boolean {
        return mapData.isEmpty()
    }
}
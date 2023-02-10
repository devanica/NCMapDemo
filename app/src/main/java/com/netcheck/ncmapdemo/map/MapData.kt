package com.netcheck.ncmapdemo.map

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
data class MapData(
    var mapBig: MutableMap<Long, Tiles> = mutableMapOf(),
    var mapMedium: MutableMap<Long, Tiles> = mutableMapOf(),
    var mapSmall: MutableMap<Long, Tiles> = mutableMapOf()
) {

    fun clearTileData() {
        synchronized(this) {
            mapBig.clear()
            mapMedium.clear()
            mapSmall.clear()
        }
    }

    fun addData(size: Int, map: Map<Long, Tiles>) {
        val data = getMap(size)
        synchronized(this) { data.putAll(map) }
    }

    /**
     * Note: Use the map only synchronized!
     *
     */
    fun getMap(size: Int): MutableMap<Long, Tiles> {
        return when (size) {
            1000 -> {
                mapBig
            }
            500 -> {
                mapMedium
            }
            else -> {
                mapSmall
            }
        }
    }
}
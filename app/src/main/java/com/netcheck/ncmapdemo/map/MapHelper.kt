package com.netcheck.ncmapdemo.map

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.netcheck.ncmapdemo.system.SystemDefsI
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import kotlin.text.Charsets.UTF_8

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
object MapHelper {
    private const val FCT = 100000.0
    private const val MINX = 0
    private const val MAXX = 1
    private const val MINY = 2
    private const val MAXY = 3

    // Web Mercator n/w corner of the map.
    // Size of square world map in meters, using WebMercator projection.
    private const val MAP_LENGTH = 2.0 * Math.PI * 6378137.0 / 2.0

    /**
     * Returns double[4] which contains the minx, miny, maxx and maxy values
     * calculate from the current position and zoom level
     *
     * @param x    The x-position of the tile
     * @param y    The y-position of the tile
     * @param zoom The zoom level of the tile
     * @return The bounding box as double[4]
     */
    fun getBoundingBox(x: Int, y: Int, zoom: Int): DoubleArray? {
        val tileSize = MAP_LENGTH / Math.pow(2.0, (zoom - 1).toDouble())
        val minX = -MAP_LENGTH + x * tileSize
        val maxX = -MAP_LENGTH + (x + 1) * tileSize
        val minY = MAP_LENGTH - (y + 1) * tileSize
        val maxY = MAP_LENGTH - y * tileSize
        val boundingBox = DoubleArray(4)
        boundingBox[MINX] = minX
        boundingBox[MINY] = minY
        boundingBox[MAXX] = maxX
        boundingBox[MAXY] = maxY
        return boundingBox
    }

    /**
     * Function to calculate the latitude size of a floq tile of the current raster
     * @param lat latitude value
     * @return latitude size
     */
    fun getSizeLat(lat: Double, tileSize: Int): Double {
        val y =
            Math.round(MAP_LENGTH / (180.0 * tileSize) * Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0))
        val lat1 =
            180.0 / Math.PI * (2.0 * Math.atan(Math.exp(180.0 * tileSize / MAP_LENGTH * y * Math.PI / 180.0)) - Math.PI / 2.0)
        val lat2 =
            180.0 / Math.PI * (2.0 * Math.atan(Math.exp(180.0 * tileSize / MAP_LENGTH * (y + 1) * Math.PI / 180.0)) - Math.PI / 2.0)
        return Math.abs(lat1 - lat2)
    }

    /**
     * Function to calculate the longitude size of a floq tile of the current raster
     * @param lng longitude value
     * @return longitude size
     */
    fun getSizeLng(lng: Double, tileSize: Int): Double {
        val x = Math.round(MAP_LENGTH / (180.0 * tileSize) * lng)
        val lng1 = 180 * tileSize / MAP_LENGTH * x
        val lng2 = 180 * tileSize / MAP_LENGTH * (x + 1)
        return Math.abs(lng1 - lng2)
    }

    /**
     * Function to calculate the exact latitude position of a floq tile
     * @param lat latitude value
     * @return exact latitude position
     */
    fun getPosLat(lat: Double, tileSize: Int): Double {
        val y =
            Math.round(MAP_LENGTH / (180.0 * tileSize) * Math.log(Math.tan((90.0 + lat) * Math.PI / 360.0)) / (Math.PI / 180.0))
        return 180.0 / Math.PI * (2.0 * Math.atan(Math.exp(180.0 * tileSize / MAP_LENGTH * y * Math.PI / 180.0)) - Math.PI / 2.0)
    }

    /**
     * Function to calculate the exact longitude position of a floq tile
     * @param lng longitude value
     * @return exact longitude position
     */
    fun getPosLng(lng: Double, tileSize: Int): Double {
        val x = Math.round(MAP_LENGTH / (180.0 * tileSize) * lng)
        return 180 * tileSize / MAP_LENGTH * x
    }

    /**
     * Function to calculate an individual key for every floq tile
     * @param pos position
     * @return key for the floq tile
     */
    fun calculateKey(pos: LatLng): Long {
        return (pos.latitude * FCT * FCT * 10.0).toLong() + (pos.longitude * FCT).toLong()
    }

    /**
     * Function to get the center of a floq tile
     * @param ll location
     * @param tileSize tileSize
     * @return center as LatLng
     */
    fun getTileCenter(ll: LatLng, tileSize: Int): LatLng? {
        val lat = getPosLat(ll.latitude, tileSize)
        val lng = getPosLng(ll.longitude, tileSize)
        return LatLng(lat, lng)
    }

    /**
     * @param x coordinate
     * @param y coordinate
     * @param zoom level
     * @return LatLngBounds for the Tile
     */
    fun tile2boundingBox(x: Int, y: Int, zoom: Int): LatLngBounds? {
        val start = LatLng(tile2lat(y + 1, zoom), tile2lon(x, zoom))
        val end = LatLng(tile2lat(y, zoom), tile2lon(x + 1, zoom))
        return LatLngBounds(start, end)
    }

    fun tile2lon(x: Int, z: Int): Double {
        return x / Math.pow(2.0, z.toDouble()) * 360.0 - 180
    }

    fun tile2lat(y: Int, z: Int): Double {
        val n = Math.PI - 2.0 * Math.PI * y / Math.pow(2.0, z.toDouble())
        return Math.toDegrees(Math.atan(Math.sinh(n)))
    }

    /**
     * Function to get the raster size from the current zoom level
     * @param zoom current zoomlvl
     */
    fun getTileTypeFromZoom(zoom: Int): Int {
        if (zoom < 10) {
            return 0
        }
        if (zoom > 14) {
            return 200
        } else if (zoom > 13) {
            return 500
        }
        return 1000
    }

    fun loadFromURL(url: URL): String? {
        var result: String? = null
        val outputStream = ByteArrayOutputStream()
        val bb = ByteArray(10000)
        var ins: InputStream? = null
        try {
            ins = url.openConnection().getInputStream()
            var i = ins.read(bb)
            while (i != -1) {
                outputStream.write(bb, 0, i)
                i = ins.read(bb)
            }
            val bytes = outputStream.toByteArray()
            result = String(bytes, UTF_8)
            if (result.contentEquals("[]")) return null
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                outputStream.close()
                ins?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    /**
     * Method to get the correct test type based on the pid.
     * @return the test type or DOWNLOAD if no match is found
     */
    fun getTestType(test: Int): SystemDefsI.EnTestType {
        for(i in SystemDefsI.EnTestType.values())
            if (i.getPID() == test) return i
        return SystemDefsI.EnTestType.DOWNLOAD
    }

    @SuppressLint("DefaultLocale")
    fun getTileResultWithUnit(v: Double, t: SystemDefsI.EnTestType, context: Context): String? {
        return if (v >= t.getLimit()) java.lang.String.format(
            "%.2f %s",
            v / t.getLimit(),
            t.getUnitBig(context)
        ) else java.lang.String.format("%.0f %s", v, t.getUnitSmall(context))
    }

}
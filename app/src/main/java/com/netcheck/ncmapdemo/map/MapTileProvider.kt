package com.netcheck.ncmapdemo.map

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Tile
import com.google.android.gms.maps.model.TileProvider
import com.netcheck.ncmapdemo.map.MapHelper.getTileTypeFromZoom
import com.netcheck.ncmapdemo.Constants
import com.netcheck.ncmapdemo.tool.SharedPrefs
import com.netcheck.ncmapdemo.system.SystemDefsI
import com.netcheck.ncmapdemo.ui.tilemap.TileMapViewModel
import org.json.JSONArray
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Semaphore
import kotlin.math.abs
import kotlin.math.roundToInt

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
class MapTileProvider(context: Context): TileProvider {
    private var handler: Handler? = null
    private var googleMap: GoogleMap? = null
    private var thisContext = context

    private var paint: Paint? = null
    private var mapData: MapData? = null

    private var BASE_URL: String? = null
    private val DIVIDER = "&"

    private var useAVG: Boolean = true
    private var filterTestType: SystemDefsI.EnTestType? = null


    constructor(opacity: Int, gm: GoogleMap?, fBAvg: Boolean, context: Context) : this(context) {
        useAVG = fBAvg
        filterTestType = MapHelper.getTestType(SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1))
        paint = Paint()
        // If map is not working just delete this looper from the brackets
        handler = Handler(Looper.myLooper()!!)
        googleMap = gm
        mapData = MapData()
        BASE_URL = "https://api.dev.netcheckapp.com/api/map?grid="
        paint!!.alpha = (opacity * 2.55).roundToInt()
    }

    /**
     * Override getTile method to draw the tiles
     *
     * */
    override fun getTile(x: Int, y: Int, zoom: Int): Tile? {
        if (zoom < 10) {
            return TileProvider.NO_TILE
        }
        val tileDim = getLatLngBoundsForTile(x, y, zoom)
        // Send url with pid here
        val tileUrl = filterTestType?.let { getURL(x, y, zoom, it.getPID()) } ?: return null
        val result = MapHelper.loadFromURL(tileUrl) ?: return null
        // Get filtered array from the server
        val jsonArray: JSONArray = try {
            JSONArray(result)
        } catch (e: Exception) {
            return null
        }
        val tileMap = getTiles(jsonArray)

        val bmp = getBitmap(tileDim, tileMap, thisContext)
        val outputStream = ByteArrayOutputStream()
        if (bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
            val tile = Tile(256, 256, outputStream.toByteArray())
            // z = 200, 500, 1000
            TileMapViewModel.GetTiles.zoom = getTileTypeFromZoom(zoom)
            mapData?.clearTileData()
            mapData?.addData(TileMapViewModel.GetTiles.zoom, tileMap)
            return tile
        }
        return null
    }

    /**
     * Create the tiles based on the http response and put them in new array
     *
     * */
    private fun getTiles(jArray: JSONArray): Map<Long, Tiles> {
        val tileMap: MutableMap<Long, Tiles> = HashMap()
        try {
            for (z in 0 until jArray.length()) {
                val jTile = jArray.getJSONObject(z)
                val avg = jTile.getDouble("value_avg")
                val best = jTile.getDouble("value_best")
                val map: MutableMap<Int, TileData> = HashMap()
                val jStringProv = jTile.getString("operator_values")
                val jArrayProv = JSONArray(jStringProv)
                //val jArrayProv = jTile.getJSONArray("operator_values")
                for (c in 0 until jArrayProv.length()) {
                    val jProvider = jArrayProv.getJSONObject(c)
                    val keys = jProvider.keys()
                    val key = keys.next()
                    val jProviderValues = jProvider.getJSONObject(key)
                    val pAvg = jProviderValues.getDouble("avg")
                    val pBest = jProviderValues.getDouble("best")
                    map[Integer.valueOf(key)] = TileData(pBest, pAvg)
                }
                val tile = Tiles(LatLng(jTile.getDouble("lat"), jTile.getDouble("lon")), avg, best, map)
                if (!tile.isEmpty()) {
                    tileMap[tile.key] = tile
                    TileMapViewModel.GetTiles.tileArray[tile.key] = tile
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return tileMap
    }

    /**
     * Draw and colorize the tiles
     *
     * */
    private fun getBitmap(tileDim: TileDimension?, map: Map<Long, Tiles>, context: Context): Bitmap {
        val bmp = Bitmap.createBitmap(tileDim!!.dimensionX, tileDim.dimensionY, Bitmap.Config.ARGB_8888)
        val cnv = Canvas(bmp)
        // Paint
        val red = Paint(Color.RED)
        red.alpha = 30
        red.style = Paint.Style.STROKE
        red.strokeWidth = 1f
        red.strokeCap = Paint.Cap.SQUARE
        red.isAntiAlias = false
        // Paint
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = false
        bmp.eraseColor(0)
        // Tile dimen
        val dimensionX = tileDim.dimensionX
        val dimensionY = tileDim.dimensionY
        val dimenX = tileDim.dimenX
        val dimenY = tileDim.dimenY
        val latLng = tileDim.latLng
        val bounds = tileDim.bounds
        val extBounds = tileDim.extBounds
        try {
            for ((ll, avg, best) in map.values) {
                if (!extBounds.contains(ll)) continue
                val posLng = ll.longitude
                val posLat = ll.latitude
                val startX = getPosFromLng(dimensionX, dimenX, bounds.southwest.longitude, posLng - latLng.longitude)
                val endX = getPosFromLng(dimensionX, dimenX, bounds.southwest.longitude, posLng + latLng.longitude)
                val startY = getPosFromLat(dimensionY, dimenY, bounds.northeast.latitude, posLat + latLng.latitude)
                val endY = getPosFromLat(dimensionY, dimenY, bounds.northeast.latitude, posLat - latLng.latitude)
                // best/avg, type, pid
                paint.color = filterTestType?.let { getColorCategory(if(useAVG) avg else best, it, context) }!!
                val rect = Rect(startX, startY, endX, endY)
                cnv.drawRect(rect, paint)
                cnv.drawRect(rect, red)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return bmp
    }

    /**
     * Present colors based on the filters
     *
     * */
    private fun getColorCategory(value: Double, test: SystemDefsI.EnTestType, context: Context): Int {
        return test.getCategory(value, context)
    }

    /**
     * Get lat lng bounds to make the tiles
     *
     * */
    private fun getLatLngBoundsForTile(x: Int, y: Int, zoom: Int): TileDimension? {
        val llb = MapHelper.tile2boundingBox(x, y, zoom)
        val tileSize = getTileTypeFromZoom(zoom)
        val p1 = Point()
        val p2 = Point()
        val sx = Semaphore(0)
        handler!!.post {
            val p = googleMap!!.projection
            val x1 = p.toScreenLocation(llb!!.northeast)
            val x2 = p.toScreenLocation(llb.southwest)
            p1[x1.x] = x1.y
            p2[x2.x] = x2.y
            sx.release()
        }
        try {
            sx.acquire()
        } catch (t: InterruptedException) {
            return null
        }
        val dimXInt = abs(p1.x - p2.x)
        val dimYInt = abs(p1.y - p2.y)
        val dimX = abs(llb!!.northeast.longitude - llb.southwest.longitude)
        val dimY = abs(llb.southwest.latitude - llb.northeast.latitude)
        val llc = LatLng(0.5 * (llb.northeast.latitude + llb.southwest.latitude),
                0.5 * (llb.northeast.longitude + llb.southwest.longitude))
        val dll = LatLng(MapHelper.getSizeLat(llc.latitude, tileSize) * 0.5,
            MapHelper.getSizeLng(llc.longitude, tileSize) * 0.5)
        val llbT = LatLngBounds(
                LatLng(llb.southwest.latitude - dll.latitude, llb.southwest.longitude - dll.longitude),
                LatLng(llb.northeast.latitude + dll.latitude, llb.northeast.longitude + dll.longitude)
        )
        return TileDimension(llb, llbT, dimXInt, dimYInt, dimX, dimY, dll, tileSize)
    }

    /**
     * Construct URL with filters (zoom, pid)
     *
     * */
    private fun getURL(x: Int, y: Int, zoom: Int, pid: Int): URL? {
        if (zoom < 10) {
            return null
        }
        val s = (BASE_URL + getTileTypeFromZoom(zoom)
                + DIVIDER + "pid=" + pid + DIVIDER + "z="
                + zoom + DIVIDER + "x=" + x + DIVIDER + "y=" + y)
        val url: URL
        url = try {
            URL(s)
        } catch (e: MalformedURLException) {
            throw AssertionError(e)
        }
        return url
    }

    /**
     * Calculation to get latitude
     *
     * */
    private fun getPosFromLat(dimPixel: Int, range: Double, lat: Double, posPoint: Double): Int {
        return (dimPixel * (lat - posPoint) / range).toInt()
    }

    /**
     * Calculation to get longitude
     *
     * */
    private fun getPosFromLng(dimPixel: Int, range: Double, lon: Double, posPoint: Double): Int {
        return (dimPixel * (posPoint - lon) / range).toInt()
    }
}
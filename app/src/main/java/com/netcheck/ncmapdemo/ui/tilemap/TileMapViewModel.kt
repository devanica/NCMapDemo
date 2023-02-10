package com.netcheck.ncmapdemo.ui.tilemap

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.netcheck.ncmapdemo.map.MapTileProvider
import com.netcheck.ncmapdemo.map.Tiles
import com.netcheck.ncmapdemo.model.Operator
import com.netcheck.ncmapdemo.tool.SharedPrefs
import com.netcheck.ncmapdemo.system.SystemTools
import com.netcheck.ncmapdemo.Constants
import com.netcheck.ncmapdemo.R

/**
 * ViewModel class for tool fragment should hold what some call "business" logic.
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic </dd>
 * <dt><b> Created:   </b></dt><dd>  02.04.2022. </dd>
 * </dl>
 ******************************************************************************
 *
 */
class TileMapViewModel : ViewModel() {
    private var tileOverlay: TileOverlay? = null
    private var tileProvider: MapTileProvider? = null

    object GetTiles {
        val operatorArray = arrayListOf<Operator>()
        val tileArray = hashMapOf<Long, Tiles>()
        var zoom = 0
    }

    fun applyMapTiles(googleMap: GoogleMap, context: Context) {
        if (SystemTools.getConnManager(context).allNetworks.isNotEmpty()) {
            loadTiles(googleMap, SharedPrefs.instance(context)!!.getBoolean(Constants.filterUseBest, true), context)
        } else {
            Toast.makeText(context, R.string.connection_not_available, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Method to load map tiles based on given filters.
     * @param googleMap
     * @param filterAB filter that defines tiles to be loaded
     * @param context
     *
     */
    fun loadTiles(googleMap: GoogleMap, filterAB: Boolean, context: Context) {
        if (SystemTools.getConnManager(context).allNetworks.isNotEmpty()) {
                tileOverlay?.let {
                    it.clearTileCache()
                    it.remove()
                }
            tileProvider = MapTileProvider(40, googleMap, filterAB, context)
            tileOverlay =  googleMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider!!))
        }
    }

    /**
     * Animate google map to location in the parameters.
     * @param googleMap
     * @param latitude position to animate camera to
     * @param longitude position to animate camera to
     * @param zoom
     *
     */
    fun moveToLocation(googleMap: GoogleMap, latitude: Double, longitude: Double, zoom: Float) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), zoom), 500, null)
    }

    fun moveToLocation(googleMap: GoogleMap, latitude: Double, longitude: Double) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(LatLng(latitude, longitude)), 500, null)
    }

}
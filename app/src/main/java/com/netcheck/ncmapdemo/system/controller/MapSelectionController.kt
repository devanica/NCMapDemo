package com.netcheck.ncmapdemo.system.controller

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.ui.IconGenerator
import com.netcheck.ncmapdemo.R
import com.netcheck.ncmapdemo.map.MapHelper
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.util.DisplayMetrics
import android.view.View
import android.view.View.GONE
import com.netcheck.ncmapdemo.ui.tilemap.TileMapFragment

/**
 * Controller class for map selections.
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic </dd>
 * <dt><b> Created:   </b></dt><dd>  13.06.2022. </dd>
 * </dl>
 ******************************************************************************
 *
 */
class MapSelectionController(private val context: Context) {
    private var currentTilePolygon: Polygon? = null

    var iconGenerator: IconGenerator? = null
    var marker: Marker? = null

    /**
     * This method will generate bubble icon around string it is provided with and on given location.
     * @param string - to show;
     * @param latLng - where on map to show it;
     *
     */
    fun createBubble(latLng: LatLng, googleMap: GoogleMap, view: View) {
        val bitmapFromLayout = createDrawableFromView(context, view)
        marker = googleMap.addMarker(MarkerOptions()
            .position(LatLng(latLng.latitude, latLng.longitude))
            .icon(BitmapDescriptorFactory.fromBitmap(bitmapFromLayout!!)))
    }

    private fun createDrawableFromView(context: Context, view: View): Bitmap? {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels)
        view.buildDrawingCache()
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun removeBubbleUI(tileMapFragment: TileMapFragment) {
        tileMapFragment.tvInfoOne.text = ""
        tileMapFragment.tvInfoTwo.text = ""
        tileMapFragment.tvInfoThree.text = ""
        tileMapFragment.tileBubble.visibility = GONE
        marker?.remove()
    }

    //TODO: customize using theme in xml
    private fun createIconGenerator(string: String): Bitmap {
        iconGenerator = IconGenerator(context)
        //iconGenerator.setStyle()
        return iconGenerator!!.makeIcon(string)
    }

    /**
     * This function implements 2 features:
     * 1. Marks the current selected tile by adding a border surrounding
     * 2. Centers the camera if tile is hit
     * @param position position
     * @param googleMap
     * @param context
     *
     */
    fun addTileFrame(position: LatLng, googleMap: GoogleMap) {
        val tileCenter = MapHelper.getTileCenter(position, MapHelper.getTileTypeFromZoom(googleMap.cameraPosition.zoom.toInt()))
        val latSizeHalf = MapHelper.getSizeLat(position.latitude, MapHelper.getTileTypeFromZoom(googleMap.cameraPosition.zoom.toInt())) / 2
        val longSizeHalf = MapHelper.getSizeLng(position.longitude, MapHelper.getTileTypeFromZoom(googleMap.cameraPosition.zoom.toInt())) / 2

        googleMap.animateCamera(CameraUpdateFactory.newLatLng(tileCenter!!))
        currentTilePolygon = googleMap.addPolygon(
            PolygonOptions().add(
                LatLng(tileCenter.latitude - latSizeHalf, tileCenter.longitude + longSizeHalf),
                LatLng(tileCenter.latitude + latSizeHalf, tileCenter.longitude + longSizeHalf),
                LatLng(tileCenter.latitude + latSizeHalf, tileCenter.longitude - longSizeHalf),
                LatLng(tileCenter.latitude - latSizeHalf, tileCenter.longitude - longSizeHalf))
                .fillColor(ContextCompat.getColor(context, android.R.color.transparent))
                .strokeColor(ContextCompat.getColor(context, R.color.dark_blue))
                .strokeWidth(3F)
                .zIndex(1F)
        )
    }

    /**
     * This function removes tile frame if any is present.
     *
     */
    private fun removeTileFrame() {
        currentTilePolygon?.remove()
    }

    /**
     * This function removes both circle and tile frame.
     *
     */
    fun clearMapSelections() {
        removeTileFrame()
    }

}
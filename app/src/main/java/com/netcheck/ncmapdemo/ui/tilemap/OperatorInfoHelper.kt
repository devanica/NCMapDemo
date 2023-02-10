package com.netcheck.ncmapdemo.ui.tilemap

import android.annotation.SuppressLint
import android.content.Context
import android.view.View.VISIBLE
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.netcheck.ncmapdemo.map.MapHelper
import com.netcheck.ncmapdemo.map.TileData
import com.netcheck.ncmapdemo.map.Tiles
import com.netcheck.ncmapdemo.Constants
import com.netcheck.ncmapdemo.tool.SharedPrefs
import com.netcheck.ncmapdemo.system.SystemDefsI
import java.lang.StringBuilder

/**
 * Helper class to find operator info.
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
class OperatorInfoHelper(private val context: Context) {
    var operators = StringBuilder()
    var best = StringBuilder()
    var average = StringBuilder()

    /**
     * This method handles tile click.
     * @param tileArray
     * @param latLng position clicked
     *
     */
    @SuppressLint("SetTextI18n")
    fun writeOperatorInfoUI(
        tileArray: MutableMap<Long, Tiles>?,
        latLng: LatLng,
        tileMapFragment: TileMapFragment
    ): String {
        if (tileArray != null) {
            for ((_, v) in tileArray) {
                val llbT = findLatLngTileBounds(v)
                if (llbT.contains(latLng)) {
                    searchOperators(v, tileMapFragment)
                    break
                }
            }
        }
        return operators.toString()
    }

    private fun searchOperators(tiles: Tiles, tileMapFragment: TileMapFragment) {
        for ((key, value) in tiles.mapData) {
            for (name in TileMapViewModel.GetTiles.operatorArray.indices) {
                checkIfOperatorAdded(key, name, tileMapFragment)
            }
            uiSetTestType(tileMapFragment)
            uiAddOperatorValue(value, context, setTestTypeFilter(), tileMapFragment)
        }
    }

    private fun checkIfOperatorAdded(key: Int, name: Int, tileMapFragment: TileMapFragment) {
        if (key == TileMapViewModel.GetTiles.operatorArray[name].id) {
            if (!operators.contains(TileMapViewModel.GetTiles.operatorArray[name].name)) {
                uiAddOperatorName(name, tileMapFragment)
            }
        }
    }

    private fun uiSetTestType(tileMapFragment: TileMapFragment) {
        when {
            SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1) == 1 -> {
                tileMapFragment.tvTitle.text = SystemDefsI.EnTestType.DOWNLOAD.name
            }
            SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1) == 2 -> {
                tileMapFragment.tvTitle.text = SystemDefsI.EnTestType.UPLOAD.name
            }
            SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1) == 3 -> {
                tileMapFragment.tvTitle.text = SystemDefsI.EnTestType.PING.name
            }
        }
    }

    private fun setTestTypeFilter(): SystemDefsI.EnTestType {
        // TODO: Check if it starts with download
        var testType: SystemDefsI.EnTestType = SystemDefsI.EnTestType.DOWNLOAD
        if (SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1) == 1) {
            testType = SystemDefsI.EnTestType.DOWNLOAD
        } else if (SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1) == 2) {
            testType = SystemDefsI.EnTestType.UPLOAD
        } else if (SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1) == 3) {
            testType = SystemDefsI.EnTestType.PING
        }
        return testType
    }

    @SuppressLint("SetTextI18n")
    private fun uiAddOperatorName(i: Int, tileMapFragment: TileMapFragment) {
        operators.append(TileMapViewModel.GetTiles.operatorArray[i].name + "\n")
        tileMapFragment.tileBubble.visibility = VISIBLE
        tileMapFragment.tvInfoOne.text = operators
    }

    private fun uiAddOperatorValue(v: TileData, context: Context,
                                   testType: SystemDefsI.EnTestType,
                                   tileMapFragment: TileMapFragment
    ) {
        tileMapFragment.tileBubble.visibility = VISIBLE
        best.append(MapHelper.getTileResultWithUnit(v.best, testType, context) + "\n")
        average.append(MapHelper.getTileResultWithUnit(v.avg, testType, context) + "\n")
        tileMapFragment.tvInfoTwo.text = average
        tileMapFragment.tvInfoThree.text = best
    }

    private fun findLatLngTileBounds(v: Tiles): LatLngBounds {
        val llT = LatLng(v.pos.latitude, v.pos.longitude)
        val dll = LatLng(
            MapHelper.getSizeLat(llT.latitude, TileMapViewModel.GetTiles.zoom) * 0.5,
            MapHelper.getSizeLng(llT.longitude, TileMapViewModel.GetTiles.zoom) * 0.5)
        return LatLngBounds(
            LatLng(llT.latitude - dll.latitude, llT.longitude - dll.longitude),
            LatLng(llT.latitude + dll.latitude, llT.longitude + dll.longitude)
        )
    }

    fun clearOperatorInfo() {
        operators.clear()
        best.clear()
        average.clear()
    }
}
package com.netcheck.ncmapdemo.system.controller

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.netcheck.ncmapdemo.Constants
import com.netcheck.ncmapdemo.R
import com.netcheck.ncmapdemo.model.Test
import com.netcheck.ncmapdemo.databinding.FragmentTileMapBinding
import com.netcheck.ncmapdemo.tool.SharedPrefs
import com.netcheck.ncmapdemo.ui.tilemap.TileMapFragment
import java.lang.StringBuilder

class TileMapUIController(private val context: Context,
                          private val binding: FragmentTileMapBinding,
                          private val selectionController: MapSelectionController
) {

    fun openFiltersUI(tileMapFragment: TileMapFragment) {
        binding.cvFilters.visibility = View.VISIBLE
        binding.ivFilter.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_launcher_foreground))
        selectionController.clearMapSelections()
        selectionController.removeBubbleUI(tileMapFragment)
    }

    /**
     * Present test results on list item selected from history fragment by the id that was sent from
     * there.
     * @param test - info;
     *
     */
    @SuppressLint("SetTextI18n")
    fun writeTestResultInfoUI(test: Test): String {
        val s = StringBuilder()
        s.append(
            "${context.resources.getString(R.string.date)} | ${formatDateTime(test.dateTime)}\n" +
            "${context.resources.getString(R.string.technology)} | ${test.technology}\n" +
            "${context.resources.getString(R.string.place)} | ${test.place}\n" +
            "${context.resources.getString(R.string.connection)} | ${test.type}\n"
        )
        return s.toString()
    }

    /**
     * Measurements are toggled using alpha level. Level 0.3f == off, level 1.0 == on.
     * @param binding
     * @param context
     *
     */
    fun toggleUserTestFilterUI() {
        if (binding.ivUserTests.alpha == 1.0f) {
            binding.ivUserTests.alpha = 0.3f
        } else {
            binding.ivUserTests.alpha = 1.0f
        }
    }

    /**
     * Method that presents zoom warning;
     * @param googleMap
     * @param binding
     *
     */
    fun updateZoomWarningUI(googleMap: GoogleMap) {
        if (googleMap.cameraPosition.zoom < 10) {
            binding.tvZoomWarning.visibility = View.VISIBLE
        } else {
            binding.tvZoomWarning.visibility = View.INVISIBLE
        }
    }

    fun applyTestTypeFilterUI() {
        applyTextColor(
            when (SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1)) {
                1 -> binding.tvFilterDownload
                2 -> binding.tvFilterUpload
                3 -> binding.tvFilterPing
                else -> { binding.tvFilterDownload }
            }, ContextCompat.getColor(context, R.color.netcheck_blue))
    }

    private fun applyTextColor(textView: TextView, color: Int)  {
        textView.setTextColor(color)
    }

    fun applyBestAvgFilterUI() {
        if (SharedPrefs.instance(context)!!.getBoolean(Constants.filterUseBest, false)) {
            binding.tvFilterBest.setTextColor(ContextCompat.getColor(context, R.color.netcheck_blue))

        } else {
            binding.tvFilterAverage.setTextColor(ContextCompat.getColor(context, R.color.netcheck_blue))
        }
    }

    fun clearTestTypeFilterColorsUI() {
        binding.tvFilterDownload.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.tvFilterUpload.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.tvFilterPing.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    fun clearBestAvgFilterColorsUI() {
        binding.tvFilterBest.setTextColor(ContextCompat.getColor(context, R.color.white))
        binding.tvFilterAverage.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    @SuppressLint("SetTextI18n")
    fun setTestFilerSelectionTextUI() {
        binding.tvTestType.text =
            "${context.resources.getText(setTextBasedOnPID(
                SharedPrefs.instance(context)!!.getInt(Constants.filterTestType, 1)))} | " +
            "${context.resources.getText(setTextBasedOnBest(
                SharedPrefs.instance(context)!!.getBoolean(Constants.filterUseBest, false)))}"
    }

    private fun setTextBasedOnPID(i: Int): Int {
        return when (i) {
            1 -> R.string.download
            2 -> R.string.upload
            3 -> R.string.ping
            else -> { 0 }
        }
    }

    private fun setTextBasedOnBest(b: Boolean): Int {
        return if (b) R.string.best else R.string.average
    }

    /**
     * Formats date and time for the map result selection.
     * @param string date&time string to be formatted.
     * @return formatted date as string.
     *
     */
    private fun formatDateTime(string: String?): String {
        if (string.isNullOrEmpty()) return ""
        val parts = string.split(" ".toRegex()).toTypedArray()
        return parts[0]
    }
}
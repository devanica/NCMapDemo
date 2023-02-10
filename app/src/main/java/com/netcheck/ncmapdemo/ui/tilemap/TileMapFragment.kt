package com.netcheck.ncmapdemo.ui.tilemap

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.*
import android.view.View.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.netcheck.ncmapdemo.map.MapHelper
import android.view.LayoutInflater
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.model.LatLng
import com.netcheck.ncmapdemo.R
import com.netcheck.ncmapdemo.system.*
import com.netcheck.ncmapdemo.Constants.filterTestType
import com.netcheck.ncmapdemo.Constants.filterUseBest
import com.netcheck.ncmapdemo.databinding.FragmentTileMapBinding
import com.netcheck.ncmapdemo.system.controller.MapSelectionController
import com.netcheck.ncmapdemo.system.controller.TileMapUIController
import com.netcheck.ncmapdemo.system.location.LocationHandler
import com.netcheck.ncmapdemo.system.location.UserLocation
import com.netcheck.ncmapdemo.system.permission.PermissionCheckI
import com.netcheck.ncmapdemo.system.permission.PermissionHandler
import com.netcheck.ncmapdemo.tool.NetworkUpdater
import com.netcheck.ncmapdemo.tool.SharedPrefs

/**
 * This fragment presents map with tiles that show connection strength.
 * Documentation: https://ncgroupnet-my.sharepoint.com/:o:/g/personal/anica_stojkovic_netcheck_de/
 *                EkRSDCCS4BdLgufPje35fOgB7mwF8I16RUa8QidITlRgvg?e=E6HqOb
 *
 ******************************************************************************
 * <dl compact>
 * <dt><b> Project:   </b></dt><dd>  NetCheck  </dd>
 * <dt><b> Author:    </b></dt><dd>  Anica Stojkovic   </dd>
 * <dt><b> Created:   </b></dt><dd>  22.10.2020. </dd>
 * </dl>
 ******************************************************************************
 *
 */
class TileMapFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnCameraMoveListener, GoogleMap.OnMapClickListener,
    PermissionCheckI {

    lateinit var tileBubble: View
    lateinit var tvTitle: TextView
    lateinit var tvInfoOne: TextView
    lateinit var tvInfoTwo: TextView
    lateinit var tvInfoThree: TextView

    val model: TileMapViewModel by viewModels()
    private var _binding: FragmentTileMapBinding? = null
    private val binding get() = _binding!!

    lateinit var tileGoogleMap: GoogleMap
    lateinit var tileMapView: MapView

    private lateinit var selectionController: MapSelectionController
    private lateinit var operatorInfoHelper: OperatorInfoHelper
    private lateinit var tileMapUIController: TileMapUIController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTileMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tileBubble = inflate(context, R.layout.tile_click_layout, null)

        tvInfoOne = tileBubble.findViewById(R.id.tv_infoOne)
        tvInfoTwo = tileBubble.findViewById(R.id.tv_infoTwo)
        tvInfoThree = tileBubble.findViewById(R.id.tv_infoThree)
        tvTitle = tileBubble.findViewById(R.id.tv_title)

        binding.presenter = this
        operatorInfoHelper = OperatorInfoHelper(requireContext())
        selectionController = MapSelectionController(requireContext())
        tileMapUIController = TileMapUIController(requireContext(), binding, selectionController)

        getViewSizes(view, savedInstanceState)
    }

    private fun getViewSizes(view: View, savedInstanceState: Bundle?) {
        initializeMapView(view, savedInstanceState)
        tileMapUIController.applyTestTypeFilterUI()
        tileMapUIController.applyBestAvgFilterUI()
        tileMapUIController.setTestFilerSelectionTextUI()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.tileGoogleMap = googleMap
        setupGoogleMap()
        setupObservers()
    }

    private fun setupObservers() {
        observeConnectivity(tileGoogleMap, requireContext(), viewLifecycleOwner)
        if (arguments == null) observeUserLocation(tileGoogleMap, viewLifecycleOwner)
    }

    private fun setupGoogleMap() {
        mapSetup(tileGoogleMap, requireContext())
        tileGoogleMap.setOnCameraMoveListener(this)
        tileGoogleMap.setOnMapClickListener(this)
    }

    /**
     * GoogleMap initial settings required for this app.
     * @param googleMap
     * @param context
     *
     */
    @SuppressLint("MissingPermission")
    fun mapSetup(googleMap: GoogleMap, context: Context) {
        googleMap.uiSettings.isRotateGesturesEnabled = false
        googleMap.uiSettings.isCompassEnabled = false
        googleMap.uiSettings.isTiltGesturesEnabled = false
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        googleMap.uiSettings.isMapToolbarEnabled = false
        googleMap.uiSettings.isIndoorLevelPickerEnabled = false
        googleMap.isMyLocationEnabled = isGranted(context)
        addMapStyle(googleMap, context)
    }

    /**
     * Add map style generated in map_style JSON file
     * @param googleMap
     * @param context
     *
     */
    fun addMapStyle(googleMap: GoogleMap, context: Context) {
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style))
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * This method will, upon reconnecting present tiles on the map and set map style.
     * @param googleMap
     * @param context
     * @param lifecycleOwner
     *
     */
    private fun observeConnectivity(googleMap: GoogleMap, context: Context, lifecycleOwner: LifecycleOwner) {
        NetworkUpdater.isNetworkDetected.observe(lifecycleOwner) {
            if (it) {
                addMapStyle(googleMap, context)
                model.loadTiles(googleMap, SharedPrefs.instance(context)!!.getBoolean(filterUseBest, true), context)
            }
        }
    }

    /**
     * User location is updated in this callback in case it was previously unknown so it had to be
     * obtained using findLocation() method first.
     * @param googleMap
     * @param lifecycleOwner
     *
     */
    private fun observeUserLocation(googleMap: GoogleMap, lifecycleOwner: LifecycleOwner) {
        UserLocation.locationUpdate.observe(lifecycleOwner) {
            model.moveToLocation(googleMap, it.latitude, it.longitude, 15f)
        }
    }

    /**
     * This method is required for mapView setup.
     * @param view
     * @param savedInstanceState
     *
     */
    private fun initializeMapView(view: View, savedInstanceState: Bundle?) {
        tileMapView = view.findViewById(R.id.mv_googleTileMap) as MapView
        tileMapView.onCreate(savedInstanceState)
        tileMapView.onResume()
        tileMapView.getMapAsync(this)
    }

    /**
     * Handle camera move, if details open, close them if tile size changed and update zoom warning.
     *
     */
    override fun onCameraMove() {
        tileMapUIController.updateZoomWarningUI(tileGoogleMap)
    }

    /**
     * Handle map click, if tile clicked present results.
     * @param position
     *
     */
    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapClick(position: LatLng) {
        closeFiltersButton()
        operatorInfoHelper.clearOperatorInfo()
        selectionController.clearMapSelections()
        selectionController.removeBubbleUI(this)
        if (tileGoogleMap.cameraPosition.zoom >= 10) {
            val operators = operatorInfoHelper.writeOperatorInfoUI(TileMapViewModel.GetTiles.tileArray, position, this)
            if (operators.isNotEmpty()) {
                selectionController.addTileFrame(position, tileGoogleMap)
                selectionController.createBubble(
                    MapHelper.getTileCenter(position,
                        MapHelper.getTileTypeFromZoom(tileGoogleMap.cameraPosition.zoom.toInt()))!!,
                        tileGoogleMap, tileBubble)
            } else {
                selectionController.clearMapSelections()
                selectionController.removeBubbleUI(this)
            }
        }
    }

    /**
     * Map control button.
     *
     */
    fun closeFiltersButton() {
        binding.cvFilters.visibility = GONE
        binding.ivFilter.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_foreground))
    }

    /**
     * Map control button.
     * Set best/avg filter in sharedPrefs for the get map rest call.
     * Apply filters and show the results on the map.
     * @param - selected rate;
     *
     */
    fun filterButton(isBest: Boolean) {
        SharedPrefs.instance(requireContext())!!.edit()?.putBoolean(filterUseBest, isBest)?.apply()
        tileMapUIController.clearBestAvgFilterColorsUI()
        tileMapUIController.applyBestAvgFilterUI()
        tileMapUIController.setTestFilerSelectionTextUI()
        model.applyMapTiles(tileGoogleMap, requireContext())
    }

    /**
     * Map control button.
     * This method opens filter selection.
     *
     */
    fun filtersButton() {
        if (binding.cvFilters.visibility == VISIBLE) {
            closeFiltersButton()
        } else {
            tileMapUIController.openFiltersUI(this)
        }
    }

    /**
     * Map control button.
     * Set test filter in sharedPrefs for the get map rest call.
     * Apply filters and show the results on the map.
     * @param checkedId - selected type;
     *
     */
    fun typeButton(checkedId: Int) {
        SharedPrefs.instance(requireContext())!!.edit()?.putInt(filterTestType,
            MapHelper.getTestType(checkedId).getPID())?.apply()
        tileMapUIController.clearTestTypeFilterColorsUI()
        tileMapUIController.applyTestTypeFilterUI()
        tileMapUIController.setTestFilerSelectionTextUI()
        model.applyMapTiles(tileGoogleMap, requireContext())
    }

    /**
     * Map control button.
     * Save user tests selection to sharedPrefs and present the values on the map based on their selection.
     *
     */
    fun userTestButton() {
        tileMapUIController.toggleUserTestFilterUI()
    }

    /**
     * Map control button.
     * Find user location on the map on myLocation button click, if location disabler it will warn
     * the user. Otherwise it will check if location is null, if it is it will try to obtain it.
     *
     */
    fun positionButton() {
        if (LocationHandler.getUserLocation() != null) {
            model.moveToLocation(
                tileGoogleMap, LocationHandler.getUserLocation()!!.latitude,
                LocationHandler.getUserLocation()!!.longitude)
            return
        }
        LocationHandler.obtainUserLocation(requireContext())
    }

    /**
     * Map control button.
     *
     */
    fun zoomInButton() {
        tileGoogleMap.animateCamera(CameraUpdateFactory.zoomIn())
    }

    /**
     * Map control button.
     *
     */
    fun zoomOutButton() {
        tileGoogleMap.animateCamera(CameraUpdateFactory.zoomOut())
    }

    /**
     * Check location permission to find user location.
     * @param context
     * @return true is permissions granted, false if not.
     *
     */
    override fun isGranted(context: Context): Boolean {
        var granted = false
        for (i in EnPermissions.values()) {
            if (i.ordinal == 2) break
            granted = PermissionHandler.permissionCheck(EnPermissions.values()[i.ordinal].getPermission(), context)
            if (!granted) {
                break
            }
        }
        return granted
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

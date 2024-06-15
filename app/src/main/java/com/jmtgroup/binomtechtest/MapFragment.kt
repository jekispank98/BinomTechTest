package com.jmtgroup.binomtechtest

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jmtgroup.binomtechtest.databinding.FragmentMapBinding
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class MapFragment : Fragment() {
    private lateinit var _binding: FragmentMapBinding
    private lateinit var mapController: IMapController
    private lateinit var locationManager: LocationManager
    private lateinit var map: MapView

    companion object {
        private const val LOCATION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
            requireActivity(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configureMap()
        setPoint()
        setListeners()
    }

    private fun configureMap() {
        map = _binding.osmMap
        map.let {
            it.setTileSource(TileSourceFactory.MAPNIK)
            it.setMultiTouchControls(true)
            mapController = it.controller
        }
        mapController.setZoom(Points.START_ZOOM)
        mapController.setCenter(Points.center)
    }

    private fun setPoint() {
        Points.randomPoint.forEach {
            addMarker(it, map)
        }
    }

    private fun setListeners() {
        _binding.apply {
            buttonZoomPlus.setOnClickListener {
                mapController.zoomOut()
            }
            buttonZoomMinus.setOnClickListener {
                mapController.zoomIn()
            }
            buttonMyLocation.setOnClickListener {
                setMyLocation()
            }
        }
    }

    private fun addMarker(geoPoint: GeoPoint, map: MapView) {
        val marker = Marker(map)
        with(marker) {
            position = geoPoint
            icon = resources.getDrawable(R.drawable.ic_sample_avatar, null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            setOnMarkerClickListener { marker, mapView ->
                setInfoWindowAnchor(Marker.ANCHOR_RIGHT, Marker.ANCHOR_BOTTOM)
                infoWindow = CustomInfoWindow(R.layout.info_window, map)
                showInfoWindow()
                openBottomSheet()
                true
            }
        }
        with(map) {
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            overlays.add(marker)
            invalidate()
        }
    }

    private fun openBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        val bottomBinding = layoutInflater.inflate(R.layout.bottom_sheet_info, null)
        bottomSheetDialog.setContentView(bottomBinding)
        bottomSheetDialog.show()
    }

    private fun setMyLocation() {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, object :
                LocationListener {
                override fun onLocationChanged(location: Location) {
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    mapController.setCenter(geoPoint)
                    Marker(map).apply {
                        position = geoPoint
                        icon = resources.getDrawable(R.drawable.ic_my_location, null)
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                        map.overlays.add(this)
                        map.invalidate()
                    }
                    locationManager.removeUpdates(this)
                }

                override fun onProviderDisabled(provider: String) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            })
        }
    }
}
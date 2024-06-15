package com.jmtgroup.binomtechtest

import android.widget.TextView
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow

class CustomInfoWindow(layoutResId: Int, mapView: MapView) : InfoWindow(layoutResId, mapView) {
    override fun onOpen(item: Any?) {
    }

    override fun onClose() {
    }
}
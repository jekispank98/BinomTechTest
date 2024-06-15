package com.jmtgroup.binomtechtest

import org.osmdroid.util.GeoPoint

object Points {
    val center = GeoPoint(59.938799, 30.314093)
    val randomPoint = listOf<GeoPoint>(
        GeoPoint(59.956148, 30.318835),
        GeoPoint(59.722333, 30.416744),
        GeoPoint(59.940070, 30.328633),
        GeoPoint(59.996712, 30.421449),
        GeoPoint(59.853357, 30.033791),
    )
    const val START_ZOOM = 10.0
}
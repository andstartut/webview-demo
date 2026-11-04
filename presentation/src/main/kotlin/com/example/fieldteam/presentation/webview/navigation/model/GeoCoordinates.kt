package com.example.fieldteam.presentation.webview.navigation.model

import android.content.Intent
import androidx.core.net.toUri

data class GeoCoordinates(
    val lat: Double,
    val lng: Double,
)

internal fun GeoCoordinates.createGoogleMapsAppIntent(
    end: GeoCoordinates,
    waypoints: List<GeoCoordinates> = emptyList(),
): Intent {
    val uri = buildUrl(start = this, end = end, waypoints = waypoints).toUri()

    return Intent(Intent.ACTION_VIEW, uri).apply {
        setPackage("com.google.android.apps.maps")
    }
}

internal fun GeoCoordinates.createGoogleMapsBrowserIntent(
    end: GeoCoordinates,
    waypoints: List<GeoCoordinates> = emptyList(),
): Intent {
    val uri = buildUrl(start = this, end = end, waypoints = waypoints).toUri()

    return Intent(Intent.ACTION_VIEW, uri)
}

private fun buildUrl(
    start: GeoCoordinates,
    end: GeoCoordinates,
    waypoints: List<GeoCoordinates>,
): String = buildString {
    append("https://www.google.com/maps/dir/?api=1")
    append("&origin=${start.lat},${start.lng}")
    append("&destination=${end.lat},${end.lng}")

    if (waypoints.isNotEmpty()) {
        val waypointsParam = waypoints.joinToString(separator = "|") { "${it.lat},${it.lng}" }
        append("&waypoints=$waypointsParam")
    }
}

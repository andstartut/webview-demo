package com.example.fieldteam.presentation.webview.navigation.model

internal data class GoogleMapsRoute(
    val start: GeoCoordinates,
    val end: GeoCoordinates,
    val waypoints: List<GeoCoordinates> = emptyList(),
)

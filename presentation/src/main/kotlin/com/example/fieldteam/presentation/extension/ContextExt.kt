package com.example.fieldteam.presentation.extension

import android.content.Context
import com.example.fieldteam.presentation.webview.navigation.model.GoogleMapsRoute
import com.example.fieldteam.presentation.webview.navigation.model.createGoogleMapsAppIntent
import com.example.fieldteam.presentation.webview.navigation.model.createGoogleMapsBrowserIntent

internal fun Context.startGoogleMapsNavigation(
    route: GoogleMapsRoute,
    startFailure: (Boolean) -> Unit,
) {
    val googleMapsAppIntent = route.start.createGoogleMapsAppIntent(route.end, route.waypoints)
    val browserIntent = route.start.createGoogleMapsBrowserIntent(route.end, route.waypoints)

    val hasFailure = runCatching {
        startActivity(
            when {
                googleMapsAppIntent.resolveActivity(packageManager) != null -> googleMapsAppIntent
                else -> browserIntent
            }
        )
    }.isFailure
    startFailure(hasFailure)
}

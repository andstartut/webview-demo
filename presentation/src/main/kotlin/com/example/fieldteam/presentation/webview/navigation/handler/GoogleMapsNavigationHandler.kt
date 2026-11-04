package com.example.fieldteam.presentation.webview.navigation.handler

import com.example.fieldteam.presentation.webview.navigation.model.GoogleMapsRoute
import kotlinx.coroutines.flow.SharedFlow

internal interface GoogleMapsNavigationHandler {

    val data: SharedFlow<GoogleMapsRoute>

    suspend fun update(googleMapsNavigation: GoogleMapsRoute)
}

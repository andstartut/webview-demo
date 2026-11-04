package com.example.fieldteam.presentation.webview.navigation.handler

import com.example.fieldteam.presentation.webview.navigation.model.GoogleMapsRoute
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

@Single
internal class SpaGoogleMapsNavigationHandler : GoogleMapsNavigationHandler {

    private val _data = MutableSharedFlow<GoogleMapsRoute>(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val data: SharedFlow<GoogleMapsRoute> = _data.asSharedFlow()

    override suspend fun update(googleMapsNavigation: GoogleMapsRoute) {
        _data.emit(googleMapsNavigation)
    }
}

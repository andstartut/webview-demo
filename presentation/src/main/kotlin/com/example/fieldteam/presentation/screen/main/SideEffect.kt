package com.example.fieldteam.presentation.screen.main

import com.example.fieldteam.presentation.webview.navigation.model.GoogleMapsRoute

internal sealed class SideEffect {
    data class LaunchPermission(val permission: String) : SideEffect()
    data class LaunchGoogleMapsNavigation(val googleMapsRoute: GoogleMapsRoute) : SideEffect()
    data class ShowToast(val messageResId: Int) : SideEffect()
    data class ShowDatePicker(
        val requestId: String,
        val currentValue: String?,
        val minValue: String?,
        val maxValue: String?,
    ) : SideEffect()

    data class ShowTimePicker(
        val requestId: String,
        val currentValue: String?,
        val minValue: String?,
        val maxValue: String?,
    ) : SideEffect()
}

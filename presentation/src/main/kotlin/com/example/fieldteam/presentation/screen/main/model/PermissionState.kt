package com.example.fieldteam.presentation.screen.main.model

import android.Manifest

internal enum class PermissionState(val id: String) {
    Location(Manifest.permission.ACCESS_FINE_LOCATION),
    Notifications(Manifest.permission.POST_NOTIFICATIONS),
}

package com.example.fieldteam.presentation.screen.main.mapper

import com.example.fieldteam.domain.model.Permission
import com.example.fieldteam.presentation.screen.main.model.PermissionState

internal fun PermissionState.toDomain(): Permission {
    return when (this) {
        PermissionState.Location -> Permission.LOCATION
        PermissionState.Notifications -> Permission.NOTIFICATIONS
    }
}

internal fun Permission.toPresentation(): PermissionState {
    return when (this) {
        Permission.LOCATION -> PermissionState.Location
        Permission.NOTIFICATIONS -> PermissionState.Notifications
    }
}

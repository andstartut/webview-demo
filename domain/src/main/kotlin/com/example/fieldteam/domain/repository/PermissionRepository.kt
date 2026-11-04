package com.example.fieldteam.domain.repository

import com.example.fieldteam.domain.model.Permission

interface PermissionRepository {
    suspend fun hasPermission(permission: Permission): Boolean
    suspend fun openAppSettings()
}

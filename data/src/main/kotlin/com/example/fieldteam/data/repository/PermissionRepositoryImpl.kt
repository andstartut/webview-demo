package com.example.fieldteam.data.repository

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.fieldteam.domain.model.Permission
import com.example.fieldteam.domain.repository.PermissionRepository
import org.koin.core.annotation.Single

@Single(binds = [PermissionRepository::class])
internal class PermissionRepositoryImpl(
    private val context: Context
) : PermissionRepository {
    
    override suspend fun hasPermission(permission: Permission): Boolean {
        val androidPermission = permission.toAndroidPermission()
        return ContextCompat.checkSelfPermission(
            context,
            androidPermission
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private fun Permission.toAndroidPermission(): String {
        return when (this) {
            Permission.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
            Permission.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.POST_NOTIFICATIONS
                } else {
                    ""
                }
            }
        }
    }
}

package com.example.fieldteam.domain.usecase

import com.example.fieldteam.domain.model.GeoPoint
import com.example.fieldteam.domain.model.Permission
import com.example.fieldteam.domain.repository.LocationRepository
import com.example.fieldteam.domain.repository.PermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import org.koin.core.annotation.Factory
import kotlin.math.abs

@Factory
class GetFilteredLocationUpdatesUseCase(
    private val locationRepository: LocationRepository,
    private val permissionRepository: PermissionRepository,
) {
    suspend operator fun invoke(): Flow<GeoPoint?> {
        val hasPermission = permissionRepository.hasPermission(Permission.LOCATION)
        if (!hasPermission) {
            return emptyFlow()
        }
        return locationRepository.getLocationUpdates()
            .distinctUntilChanged { old, new ->
                if (old == null || new == null) return@distinctUntilChanged false

                abs(old.lat - new.lat) < MIN_LOCATION_DELTA
                        && abs(old.lng - new.lng) < MIN_LOCATION_DELTA
            }
    }

    companion object {
        private const val MIN_LOCATION_DELTA = 0.00005
    }
}

package com.example.fieldteam.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.example.fieldteam.domain.model.GeoPoint
import com.example.fieldteam.domain.repository.LocationRepository
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.annotation.Single

@Single(binds = [LocationRepository::class])
internal class LocationRepositoryImpl(
    context: Context,
) : LocationRepository {

    companion object {
        private const val LOCATION_UPDATE_INTERVAL_MS = 5000L
        private const val LOCATION_MIN_UPDATE_INTERVAL_MS = 3000L
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<GeoPoint?> = callbackFlow {
        val locationRequest = LocationRequest
            .Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                LOCATION_UPDATE_INTERVAL_MS
            )
            .setWaitForAccurateLocation(true)
            .setMinUpdateIntervalMillis(LOCATION_MIN_UPDATE_INTERVAL_MS)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation
                    ?.toGeoPoint()
                    ?.let { trySend(it) }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )

        try {
            awaitCancellation()
        } finally {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    private fun Location.toGeoPoint(): GeoPoint =
        GeoPoint(lat = latitude, lng = longitude)
}

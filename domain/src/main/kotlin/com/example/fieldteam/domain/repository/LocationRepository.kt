package com.example.fieldteam.domain.repository

import com.example.fieldteam.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocationUpdates(): Flow<GeoPoint?>
}

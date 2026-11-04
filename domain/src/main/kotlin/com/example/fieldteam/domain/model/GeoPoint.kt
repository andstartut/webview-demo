package com.example.fieldteam.domain.model

data class GeoPoint(
    val lat: Double,
    val lng: Double,
) {
    fun toJson(): String = """{"lat":$lat,"lng":$lng}"""
}

package com.example.fieldteam.data.datastore

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenData(
    val token: String = ""
)

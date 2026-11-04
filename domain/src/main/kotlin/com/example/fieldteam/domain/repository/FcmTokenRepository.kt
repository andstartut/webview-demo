package com.example.fieldteam.domain.repository

interface FcmTokenRepository {
    suspend fun getToken(): String?
    suspend fun saveToken(token: String)
}

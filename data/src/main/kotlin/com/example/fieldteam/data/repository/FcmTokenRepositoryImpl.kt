package com.example.fieldteam.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.fieldteam.data.datastore.FcmTokenData
import com.example.fieldteam.data.datastore.FcmTokenSerializer
import com.example.fieldteam.domain.repository.FcmTokenRepository
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

private val Context.fcmTokenDataStore: DataStore<FcmTokenData> by dataStore(
    fileName = "fcm_token.pb",
    serializer = FcmTokenSerializer
)

@Factory(binds = [FcmTokenRepository::class])
internal class FcmTokenRepositoryImpl(
    context: Context
) : FcmTokenRepository {

    private val dataStore: DataStore<FcmTokenData> = context.fcmTokenDataStore

    override suspend fun getToken(): String? {
        val data = dataStore.data.first()
        return data.token.takeIf { it.isNotEmpty() }
    }

    override suspend fun saveToken(token: String) {
        dataStore.updateData { current ->
            current.copy(token = token)
        }
    }
}

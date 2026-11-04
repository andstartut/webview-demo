package com.example.fieldteam.firebase.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.example.fieldteam.domain.usecase.UpdateFcmTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
class FirebaseMessagingInitializer(
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase,
) {

    fun initialize() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCMService", "Failed to get FCM token", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("FCMService", "FCM Token: $token")
            CoroutineScope(Dispatchers.IO).launch {
                updateFcmTokenUseCase(token)
            }
        }
    }
}

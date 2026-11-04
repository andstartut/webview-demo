package com.example.fieldteam.firebase.messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.example.fieldteam.domain.usecase.UpdateFcmTokenUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class FirebaseMessagingService : FirebaseMessagingService() {

    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "New FCM token : $token")

        CoroutineScope(Dispatchers.IO).launch {
            updateFcmTokenUseCase(token)
        }
    }
}

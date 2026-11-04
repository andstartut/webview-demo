package com.example.fieldteam

import android.app.Application
import com.example.fieldteam.di.appModules
import com.example.fieldteam.firebase.messaging.FirebaseMessagingInitializer
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    private val firebaseMessagingInitializer: FirebaseMessagingInitializer by inject()

    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@App)
            modules(appModules)
        }

        firebaseMessagingInitializer.initialize()
    }
}

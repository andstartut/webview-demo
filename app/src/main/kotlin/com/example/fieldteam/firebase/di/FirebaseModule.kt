package com.example.fieldteam.firebase.di

import com.example.fieldteam.firebase.messaging.FirebaseMessagingInitializer
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.example.fieldteam.firebase")
class FirebaseModule {

    @Suppress("unused")
    @Single(createdAtStart = true)
    fun provideFirebaseMessagingInitializer(initializer: FirebaseMessagingInitializer): FirebaseMessagingInitializer {
        initializer.initialize()
        return initializer
    }
}

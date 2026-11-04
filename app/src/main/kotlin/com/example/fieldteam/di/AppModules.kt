package com.example.fieldteam.di

import com.example.fieldteam.data.di.DataModule
import com.example.fieldteam.domain.di.DomainModule
import com.example.fieldteam.presentation.di.PresentationModule
import com.example.fieldteam.firebase.di.FirebaseModule
import org.koin.ksp.generated.module

val appModules = listOf(
    DomainModule().module,
    DataModule().module,
    PresentationModule().module,
    FirebaseModule().module,
)

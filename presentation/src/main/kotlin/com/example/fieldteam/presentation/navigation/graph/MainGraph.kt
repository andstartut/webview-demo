package com.example.fieldteam.presentation.navigation.graph

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.example.fieldteam.presentation.screen.main.MainRoute
import com.example.fieldteam.presentation.screen.main.mainDestination
import kotlinx.serialization.Serializable

@Serializable
data object MainGraph

fun NavGraphBuilder.mainGraph() {
    navigation<MainGraph>(startDestination = MainRoute) {
        mainDestination()
    }
}

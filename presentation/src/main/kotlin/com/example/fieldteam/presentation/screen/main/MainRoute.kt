package com.example.fieldteam.presentation.screen.main

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Serializable
internal data object MainRoute

internal fun NavGraphBuilder.mainDestination() {
    composable<MainRoute> {
        val viewModel: MainViewModel = koinViewModel()

        MainScreen(viewModel = viewModel)
    }
}

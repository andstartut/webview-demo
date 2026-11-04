package com.example.fieldteam.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.fieldteam.presentation.navigation.graph.MainGraph
import com.example.fieldteam.presentation.navigation.graph.mainGraph

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = MainGraph,
        modifier = modifier,
    ) {
        mainGraph()
    }
}

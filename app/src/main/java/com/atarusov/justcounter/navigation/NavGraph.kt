package com.atarusov.justcounter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.atarusov.justcounter.features.counters_screen.presentation.ui.CounterListScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CounterScreen.route,
    ) {
        composable(
            route = Screen.CounterScreen.route,
        ) {
            CounterListScreen()
        }
    }
}
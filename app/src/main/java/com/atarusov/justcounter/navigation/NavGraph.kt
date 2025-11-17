package com.atarusov.justcounter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.atarusov.justcounter.features.counters_screen.presentation.ui.CounterListScreen
import kotlinx.serialization.Serializable

@Serializable
object CounterListScreen

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = CounterListScreen,
    ) {
        composable<CounterListScreen> { CounterListScreen() }
    }
}
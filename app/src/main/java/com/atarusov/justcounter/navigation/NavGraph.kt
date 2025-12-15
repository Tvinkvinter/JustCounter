package com.atarusov.justcounter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.atarusov.justcounter.common.CounterColor
import com.atarusov.justcounter.features.counter_full_screen.presentation.CounterFullScreen
import com.atarusov.justcounter.features.counter_list_screen.presentation.CounterListScreen
import com.atarusov.justcounter.features.edit_dialog.presentation.EditCounterDialog
import kotlinx.serialization.Serializable

@Serializable
data class CounterFullScreen(
    val counterId: String
)

@Serializable
object CounterListScreenRoute

@Serializable
data class EditCounterDialogRoute(
    val title: String,
    val value: Int,
    val color: CounterColor,
    val steps: List<Int>,
    val counterId: String
)

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = CounterListScreenRoute,
    ) {
        composable<CounterFullScreen> {
            CounterFullScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditDialog = { counter ->
                    val route = with(counter) {
                        EditCounterDialogRoute(title, value, color, steps, id)
                    }
                    navController.navigate(route)
                },
            )
        }

        composable<CounterListScreenRoute> {
            CounterListScreen(
                onNavigateToCounterFullScreen = { counter ->
                    val route = CounterFullScreen(counter.id)
                    navController.navigate(route)
                },
                onNavigateToEditDialog = { counter ->
                    val route = with(counter) {
                        EditCounterDialogRoute(title, value, color, steps, id)
                    }
                    navController.navigate(route)
                }
            )
        }

        dialog<EditCounterDialogRoute> {
            EditCounterDialog(onEditDialogClose = { navController.popBackStack() })
        }
    }
}
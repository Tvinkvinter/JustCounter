package com.atarusov.justcounter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.atarusov.justcounter.features.counter_full_screen.presentation.ui.CounterFullScreen
import com.atarusov.justcounter.features.counter_list_screen.presentation.ui.CounterListScreen
import com.atarusov.justcounter.features.edit_dialog.presentation.ui.EditCounterDialog
import com.atarusov.justcounter.ui.theme.CounterColor
import kotlinx.serialization.Serializable

@Serializable
data class CounterFullScreen(
    val title: String,
    val value: Int,
    val color: CounterColor,
    val steps: List<Int>,
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
                    val route = with(counter) { CounterFullScreen(title, value, color, steps, id) }
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
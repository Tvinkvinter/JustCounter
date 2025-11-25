package com.atarusov.justcounter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.atarusov.justcounter.features.counter_list_screen.ui.CounterListScreen
import com.atarusov.justcounter.features.edit_dialog.ui.edit_counter_dialog.EditCounterDialog
import com.atarusov.justcounter.ui.theme.CounterColor
import kotlinx.serialization.Serializable

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
        composable<CounterListScreenRoute> {
            CounterListScreen(
                onNavigateToEditDialog = { counter ->
                    val route = EditCounterDialogRoute(
                        counter.title, counter.value, counter.color, counter.steps, counter.id
                    )
                    navController.navigate(route)
                }
            )
        }

        dialog<EditCounterDialogRoute> {
            EditCounterDialog(onEditDialogClose = { navController.popBackStack() })
        }
    }
}
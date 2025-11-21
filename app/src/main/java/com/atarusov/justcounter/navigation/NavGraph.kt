package com.atarusov.justcounter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.atarusov.justcounter.features.counters_screen.ui.CounterListScreen
import com.atarusov.justcounter.features.edit_dialog.ui.edit_counter_dialog.EditCounterDialog
import kotlinx.serialization.Serializable

@Serializable
object CounterListScreen

@Serializable
data class EditCounterDialog(val counterId: String)

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = CounterListScreen,
    ) {
        composable<CounterListScreen> {
            CounterListScreen(
                onNavigateToEditDialog = { navController.navigate(EditCounterDialog(it)) }
            )
        }

        dialog<EditCounterDialog> {
            EditCounterDialog(onEditDialogClose = { navController.popBackStack() })
        }
    }
}
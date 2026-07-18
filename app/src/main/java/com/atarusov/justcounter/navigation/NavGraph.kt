package com.atarusov.justcounter.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.atarusov.justcounter.common.CounterColor
import com.atarusov.justcounter.features.counter_full_screen.presentation.CounterFullScreen
import com.atarusov.justcounter.features.counter_list_screen.presentation.CounterListScreen
import com.atarusov.justcounter.features.edit_dialog.presentation.EditCounterDialog
import com.atarusov.justcounter.features.edit_dialog.presentation.EditCounterDialogViewModel
import com.atarusov.justcounter.shared_features.analytics.AnalyticsDestination
import com.atarusov.justcounter.shared_features.analytics.AnalyticsTracker
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
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
    navController: NavHostController,
    analyticsTracker: AnalyticsTracker,
) {
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow
            .mapNotNull { entry ->
                val route = entry.destination.route
                when {
                    route.hasRoutePrefix<CounterListScreenRoute>() ->
                        AnalyticsDestination.CounterList
                    route.hasRoutePrefix<CounterFullScreen>() ->
                        AnalyticsDestination.CounterFullscreen
                    route.hasRoutePrefix<EditCounterDialogRoute>() ->
                        AnalyticsDestination.EditCounterDialog
                    else -> null
                }
            }
            .distinctUntilChanged()
            .collect(analyticsTracker::trackScreen)
    }

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
                },
                onDrawerVisibilityChanged = { isVisible ->
                    analyticsTracker.trackScreen(
                        if (isVisible) AnalyticsDestination.CategoryDrawer
                        else AnalyticsDestination.CounterList
                    )
                },
            )
        }

        dialog<EditCounterDialogRoute> { backStackEntry ->
            val viewModel = hiltViewModel<EditCounterDialogViewModel>(backStackEntry)

            DisposableEffect(backStackEntry) {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_DESTROY) {
                        viewModel.onDialogDismissed()
                    }
                }
                backStackEntry.lifecycle.addObserver(observer)

                onDispose {
                    backStackEntry.lifecycle.removeObserver(observer)
                }
            }

            EditCounterDialog(
                onEditDialogClose = { navController.popBackStack() },
                viewModel = viewModel,
            )
        }
    }
}

private inline fun <reified T : Any> String?.hasRoutePrefix(): Boolean =
    this?.startsWith(T::class.qualifiedName.orEmpty()) == true

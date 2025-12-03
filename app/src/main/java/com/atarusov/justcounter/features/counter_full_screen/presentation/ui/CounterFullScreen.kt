package com.atarusov.justcounter.features.counter_full_screen.presentation.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_full_screen.presentation.CounterFullScreenViewModel
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.State
import com.atarusov.justcounter.features.counter_full_screen.presentation.ui.callbacks.CounterFullScreenCallbacks
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.dangerRed

@Composable
fun CounterFullScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditDialog: (counter: Counter) -> Unit,
    viewModel: CounterFullScreenViewModel = hiltViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvents.collect { event ->
            when (event) {
                OneTimeEvent.NavigateBack -> onNavigateBack()
                is OneTimeEvent.OpenEditCounterDialog -> onNavigateToEditDialog(event.counter)
            }
        }
    }

    CounterFullScreenUI(state, viewModel::onAction)
}

@Composable
fun CounterFullScreenUI(
    state: State,
    onAction: (Action) -> Unit,
) {
    Scaffold(
        topBar = {
            CounterFullScreenTopAppBar(
                removeMode = state.removeMode,
                onBackPressed = { onAction(Action.BackPressed) },
                onRemoveModeSwitch = { onAction(Action.SwitchRemoveMode) }
            )
        },
    ) { paddingValues ->
        val counterFullScreenCallbacks = CounterFullScreenCallbacks(
            onPLusClick = { step ->
                onAction(Action.PlusClick(state.counter.id, state.counter.value, step))
            },
            onMinusClick = { step ->
                onAction(Action.MinusClick(state.counter.id, state.counter.value, step))
            },
            onShrinkClick = { onAction(Action.BackPressed) },
            onEditClick = { onAction(Action.OpenCounterEditDialog(state.counter)) },
            onRemoveClick = { onAction(Action.RemoveCounter(state.counter.id)) },
        )

        CounterFullScreenCard(
            state = state.counter,
            removeMode = state.removeMode,
            callbacks = counterFullScreenCallbacks,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CounterFullScreenTopAppBar(
    removeMode: Boolean,
    onBackPressed: () -> Unit,
    onRemoveModeSwitch: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge
            )
        },
        modifier = Modifier.shadow(Dimensions.Elevation.topAppBar),
        navigationIcon = {
            IconButton(
                onClick = onBackPressed,
                modifier = Modifier
                    .padding(horizontal = Dimensions.Spacing.small)
                    .size(Dimensions.Size.medium)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back_arrow),
                    contentDescription = stringResource(R.string.counter_list_screen_add_btn_description),
                    modifier = Modifier.size(Dimensions.Size.small)
                )
            }
        },
        actions = {
            IconButton(
                onClick = onRemoveModeSwitch,
                modifier = Modifier
                    .padding(end = Dimensions.Spacing.small)
                    .size(Dimensions.Size.medium),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (removeMode) dangerRed else MaterialTheme.colorScheme.onSurface
                )
            ) {
                Icon(
                    painter = painterResource(
                        if (removeMode) R.drawable.ic_trash_can_opened
                        else R.drawable.ic_trash_can
                    ),
                    contentDescription = stringResource(R.string.counter_list_screen_add_btn_description),
                    modifier = Modifier.size(Dimensions.Size.small)
                )
            }
        }
    )
}

@Preview
@Composable
fun CounterFullScreenPreview() {
    JustCounterTheme {
        CounterFullScreenUI(State.getPreviewState(false)) {}
    }
}

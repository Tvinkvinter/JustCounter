package com.atarusov.justcounter.features.counters_screen.presentation.ui

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counters_screen.presentation.ui.callbacks.CounterItemCallbacks
import com.atarusov.justcounter.features.counters_screen.presentation.ui.callbacks.EditCounterDialogCallbacks
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditCounterDialog
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterListScreenViewModel
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.RemoveRed
import com.atarusov.justcounter.ui.theme.TransparentTextSelectionColors
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun CounterListScreen(viewModel: CounterListScreenViewModel = hiltViewModel()) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val state by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvents.collect { event ->
            when (event) {
                OneTimeEvent.ClearFocus -> focusManager.clearFocus(force = true)
                is OneTimeEvent.ShowTitleInputError -> {
                    val errorMessage = context.getString(R.string.counter_screen_empty_title_error)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    CompositionLocalProvider(LocalTextSelectionColors provides TransparentTextSelectionColors) {
        Scaffold(
            topBar = {
                CounterListTopAppBar(
                    removeMode = state.removeMode,
                    onRemoveModeSwitch = { viewModel.onAction(Action.SwitchRemoveMode) }
                )
            },
            floatingActionButton = {
                CounterListFAB(
                    onClick = { viewModel.onAction(Action.AddCounter) },
                    isVisible = !state.removeMode
                )
            },
        ) { paddingValues ->
            CounterList(
                paddingValues = paddingValues,
                removeMode = state.removeMode,
                counterItems = state.counterItems,
                onAction = { viewModel.onAction(it) }
            )
        }

        state.editDialog?.let { dialogState ->
            val editDialogCallbacks = EditCounterDialogCallbacks (
                onTitleInput = { input ->
                    viewModel.onAction(Action.TitleInput(dialogState.itemState.counterId, input))
                },
                onTitleInputDone = { input ->
                    viewModel.onAction(Action.TitleInputDone(dialogState.itemState.counterId, input))
                },
                onValueInput = { input ->
                    viewModel.onAction(Action.ValueInput(dialogState.itemState.counterId, input))
                },
                onValueInputDone = { input ->
                    viewModel.onAction(Action.ValueInputDone(dialogState.itemState.counterId, input))
                },
                onStepInput = {index, input ->
                    viewModel.onAction(Action.StepInput(dialogState.itemState.counterId, index, input))
                },
                onStepInputDone = { viewModel.onAction(Action.StepInputDone) },
                onRemoveStep = {viewModel.onAction(Action.RemoveStep)},
                onAddStep = { viewModel.onAction(Action.AddStep) },
                onColorSelected = { selectedColor ->
                    viewModel.onAction(Action.ChangeColor(dialogState.itemState.counterId, selectedColor))
                },
                onDismiss = { viewModel.onAction(Action.CloseCounterEditDialog(dialogState, true)) },
                onConfirm = { viewModel.onAction(Action.CloseCounterEditDialog(dialogState, false)) }
            )

            EditCounterDialog(
                state = dialogState,
                events = viewModel.screenEvents,
                callbacks = editDialogCallbacks
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CounterListTopAppBar(
    removeMode: Boolean,
    onRemoveModeSwitch: () -> Unit
) {
    TopAppBar(
        title = { Text("Test") },
        modifier = Modifier.shadow(Dimensions.Elevation.topAppBar),
        actions = {
            IconButton(
                onClick = onRemoveModeSwitch,
                modifier = Modifier.size(Dimensions.Size.medium),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = if (removeMode) RemoveRed else Color.DarkGray
                )
            ) {
                Icon(
                    painter = painterResource(
                        if (removeMode) R.drawable.ic_trash_can_opened
                        else R.drawable.ic_trash_can
                    ),
                    contentDescription = stringResource(R.string.counter_screen_add_btn_description),
                    modifier = Modifier.size(Dimensions.Size.small)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        ),
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    )
}

@Composable
fun CounterListFAB(
    onClick: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 100.dp,
        label = "fabOffset"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.offset(x=0.dp, y=offsetY)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(R.string.counter_screen_add_btn_description)
        )
    }
}

@Composable
fun CounterList(
    paddingValues: PaddingValues,
    removeMode: Boolean,
    counterItems: List<CounterItem>,
    onAction: (action: Action) -> Unit
) {
    val hapticFeedback = LocalHapticFeedback.current

    val lazyGridState = rememberLazyGridState()
    val reorderableLazyGridState =
        rememberReorderableLazyGridState(lazyGridState) { first, second ->
            onAction(Action.SwapCounters(first.index, second.index))
        }

    LazyVerticalGrid(
        state = lazyGridState,
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = Dimensions.Spacing.small),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small)
    ) {

        items(
            items = counterItems,
            key = { it.counterId },
        ) { counterItem ->
            val counterItemCallbacks = CounterItemCallbacks(
                onPLusClick = { step ->
                    onAction(Action.PlusClick(counterItem.counterId, step, counterItem.valueField))
                },
                onMinusClick = { step ->
                    onAction(Action.MinusClick(counterItem.counterId, step, counterItem.valueField))
                },
                onEditClick = { onAction(Action.OpenCounterEditDialog(counterItem.counterId)) },
                onInputValue = { onAction(Action.ValueInput(counterItem.counterId, it)) },
                onInputValueDone = { onAction(Action.ValueInputDone(counterItem.counterId, it)) },
                onRemoveClick = { onAction(Action.RemoveCounter(counterItem.counterId)) },
            )

            ReorderableItem(
                state = reorderableLazyGridState,
                key = counterItem.counterId
            ) { isDragging ->
                CounterItem(
                    state = counterItem,
                    removeMode = removeMode,
                    dragMode = isDragging,
                    callbacks = counterItemCallbacks,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimensions.Spacing.small)
                        .longPressDraggableHandle(
                            onDragStarted = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            onDragStopped = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.GestureEnd)
                            },
                        )
                )
            }
        }
    }
}


@Composable
@Preview(showBackground = true)
private fun CounterTopAppBarPreview() {
    JustCounterTheme {
        Column {
            CounterListTopAppBar(true, {})
            CounterListTopAppBar(false, {})
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun CounterScreenFABPreview() {
    JustCounterTheme {
        CounterListFAB({}, true)
    }
}
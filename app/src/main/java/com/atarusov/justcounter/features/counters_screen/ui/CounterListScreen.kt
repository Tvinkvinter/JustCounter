package com.atarusov.justcounter.features.counters_screen.ui

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
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.features.counters_screen.mvi.entities.Action
import com.atarusov.justcounter.features.counters_screen.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counters_screen.mvi.entities.State
import com.atarusov.justcounter.features.counters_screen.ui.callbacks.CounterItemCallbacks
import com.atarusov.justcounter.features.counters_screen.viewModel.CounterListScreenViewModel
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.dangerRed
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun CounterListScreen(
    onNavigateToEditDialog: (counter: Counter) -> Unit,
    viewModel: CounterListScreenViewModel = hiltViewModel()
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val lazyGridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        viewModel.screenEvents.collect { event ->
            when (event) {
                OneTimeEvent.ShowDragTip -> {
                    val errorMessage = context.getString(R.string.counter_screen_drag_tip)
                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                }
                OneTimeEvent.ClearFocus -> focusManager.clearFocus(force = true)
                OneTimeEvent.ScrollDown -> lazyGridState.animateScrollToItem(state.counters.lastIndex)
                is OneTimeEvent.OpenEditCounterDialog -> onNavigateToEditDialog(event.counter)
            }
        }
    }

    CounterListScreenUI(
        state = state,
        onAction = viewModel::onAction,
        lazyGridState = lazyGridState
    )
}

@Composable
private fun CounterListScreenUI(
    state: State,
    onAction: (Action) -> Unit,
    lazyGridState: LazyGridState
) {
    Scaffold(
        topBar = {
            CounterListTopAppBar(
                removeMode = state.removeMode,
                onRemoveModeSwitch = { onAction(Action.SwitchRemoveMode) }
            )
        },
        floatingActionButton = {
            CounterListFAB(
                onClick = { onAction(Action.AddCounter) },
                isVisible = !state.removeMode
            )
        },
    ) { paddingValues ->
        CounterList(
            lazyGridState = lazyGridState,
            paddingValues = paddingValues,
            removeMode = state.removeMode,
            counters = state.counters,
            onAction = onAction
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CounterListTopAppBar(
    removeMode: Boolean,
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
                    contentDescription = stringResource(R.string.counter_screen_add_btn_description),
                    modifier = Modifier.size(Dimensions.Size.small)
                )
            }
        },
        scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    )
}

@Composable
private fun CounterListFAB(
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
        modifier = modifier.offset { IntOffset(0, offsetY.toPx().toInt()) },
        elevation = FloatingActionButtonDefaults.elevation(Dimensions.Elevation.fab),
        shape = RoundedCornerShape(Dimensions.Radius.extraLarge)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = stringResource(R.string.counter_screen_add_btn_description)
        )
    }
}

@Composable
private fun CounterList(
    lazyGridState: LazyGridState,
    paddingValues: PaddingValues,
    removeMode: Boolean,
    counters: List<Counter>,
    onAction: (action: Action) -> Unit
) {
    val reorderableLazyGridState =
        rememberReorderableLazyGridState(lazyGridState) { first, second ->
            onAction(Action.SwapCounters(first.index, second.index))
        }

    LazyVerticalGrid(
        state = lazyGridState,
        contentPadding = PaddingValues(top = Dimensions.Spacing.small, bottom = 100.dp),
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = Dimensions.Spacing.medium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.small, Alignment.CenterHorizontally)
    ) {

        items(
            items = counters,
            key = { it.id },
        ) { counter ->
            val counterItemCallbacks = CounterItemCallbacks(
                onCounterTap = { onAction(Action.TitleTap) },
                onPLusClick = { step ->
                    onAction(Action.PlusClick(counter.id, counter.value, step))
                },
                onMinusClick = { step ->
                    onAction(Action.MinusClick(counter.id, counter.value, step))
                },
                onEditClick = { onAction(Action.OpenCounterEditDialog(counter)) },
                onRemoveClick = { onAction(Action.RemoveCounter(counter.id)) },
            )

            ReorderableItem(
                state = reorderableLazyGridState,
                key = counter.id
            ) { isDragging ->
                CounterItem(
                    state = counter,
                    removeMode = removeMode,
                    dragMode = isDragging,
                    callbacks = counterItemCallbacks,
                    modifier = Modifier.fillMaxWidth(),
                    reorderableScope = this,
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

@Composable
@Preview(showBackground = true)
private fun CounterListScreenPreview() {
    JustCounterTheme {
        CounterListScreenUI(
            state = State.getPreviewState(false),
            onAction = {},
            lazyGridState = LazyGridState()
        )
    }
}
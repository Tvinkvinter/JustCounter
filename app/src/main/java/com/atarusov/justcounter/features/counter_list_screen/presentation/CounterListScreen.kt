package com.atarusov.justcounter.features.counter_list_screen.presentation

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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.category_drawer.presentation.Drawer
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.State
import com.atarusov.justcounter.features.counter_list_screen.presentation.components.CounterItem
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.dangerRed
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState

@Composable
fun CounterListScreen(
    onNavigateToCounterFullScreen: (counter: Counter) -> Unit,
    onNavigateToEditDialog: (counter: Counter) -> Unit,
    viewModel: CounterListScreenViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val state by viewModel.screenState.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val lazyGridState = rememberLazyGridState()

    val dragTipMessage = stringResource(R.string.counter_list_screen_drag_tip)

    LaunchedEffect(Unit) {
        viewModel.screenEvents.collect { event ->
            when (event) {
                is OneTimeEvent.ChangeCategory -> viewModel.setCategoryId(event.id)
                OneTimeEvent.ShowDragTip -> {
                    Toast.makeText(context, dragTipMessage, Toast.LENGTH_SHORT).show()
                }
                OneTimeEvent.ScrollDown -> lazyGridState.animateScrollToItem(state.counters.lastIndex)
                is OneTimeEvent.NavigateToCounterFullScreen -> onNavigateToCounterFullScreen(event.counter)
                is OneTimeEvent.OpenEditCounterDialog -> onNavigateToEditDialog(event.counter)
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Drawer(
                onCategorySelect = { categoryId, closeDrawer ->
                    viewModel.onAction(Action.ChangeCategory(categoryId))
                    if (closeDrawer) scope.launch {
                        delay(300)
                        drawerState.close()
                    }
                },
            )
        }
    ) {
        CounterListScreenUI(
            state = state,
            onAction = viewModel::onAction,
            onDrawerIconClick = { scope.launch { drawerState.open() } },
            lazyGridState = lazyGridState
        )
    }
}

@Composable
private fun CounterListScreenUI(
    state: State,
    onAction: (Action) -> Unit,
    onDrawerIconClick: () -> Unit,
    lazyGridState: LazyGridState
) {
    Scaffold(
        topBar = {
            CounterListTopAppBar(
                removeMode = state.removeMode,
                categoryName = state.category?.name  ?: stringResource(R.string.app_name),
                onDrawerIconClick = onDrawerIconClick,
                onRemoveModeSwitch = { onAction(Action.SwitchRemoveMode) }
            )
        },
        floatingActionButton = {
            CounterListFAB(
                onClick = { onAction(Action.AddCounter(state.category?.id)) },
                isVisible = !state.removeMode
            )
        },
    ) { paddingValues ->
        CounterList(
            lazyGridState = lazyGridState,
            paddingValues = paddingValues,
            removeMode = state.removeMode,
            categoryId = state.category?.id,
            counters = state.counters,
            onAction = onAction
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CounterListTopAppBar(
    removeMode: Boolean,
    categoryName: String,
    onDrawerIconClick: () -> Unit,
    onRemoveModeSwitch: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.headlineLarge
            )
        },
        modifier = Modifier.shadow(Dimensions.Elevation.topAppBar),
        navigationIcon = {
            IconButton(
                onClick = onDrawerIconClick,
                modifier = Modifier
                    .padding(horizontal = Dimensions.Spacing.small)
                    .size(Dimensions.Size.medium)
            ) {
                Icon (
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = stringResource(R.string.counter_list_screen_open_drawer),
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
                    contentDescription = stringResource(R.string.counter_list_screen_trash_can_btn_description),
                    modifier = Modifier.size(Dimensions.Size.small)
                )
            }
        }
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
            contentDescription = stringResource(R.string.counter_list_screen_add_btn_description)
        )
    }
}

@Composable
private fun CounterList(
    lazyGridState: LazyGridState,
    paddingValues: PaddingValues,
    removeMode: Boolean,
    categoryId: Int?,
    counters: List<Counter>,
    onAction: (action: Action) -> Unit
) {
    val reorderableLazyGridState =
        rememberReorderableLazyGridState(lazyGridState) { first, second ->
            onAction(Action.SwapCounters(categoryId, first.index, second.index))
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
                onExpandClick = { onAction(Action.ExpandCounter(counter)) },
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
            CounterListTopAppBar(true, "Category name", {}) {}
            CounterListTopAppBar(false, "Category name", {}) {}
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
            onDrawerIconClick = {},
            lazyGridState = LazyGridState()
        )
    }
}

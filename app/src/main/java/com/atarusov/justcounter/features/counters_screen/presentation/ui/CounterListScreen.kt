package com.atarusov.justcounter.features.counters_screen.presentation.ui

import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditCounterDialog
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterListScreenViewModel
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.OneTimeEvent
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.RemoveRed
import com.atarusov.justcounter.ui.theme.TransparentTextSelectionColors
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterListAction as Action

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
                    onClick = { viewModel.onAction(Action.CreateNewCounter) },
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
            EditCounterDialog(
                state = dialogState,
                events = viewModel.screenEvents,
                onTitleInput = { input ->
                    viewModel.onAction(Action.TitleInput(dialogState.itemState.counterId, input))
                },
                onTitleInputDone = { counterId -> viewModel.onAction(Action.TitleInputDone(counterId)) },
                onValueInput = { input ->
                    viewModel.onAction(Action.ValueInput(dialogState.itemState.counterId, input))
                },
                onColorSelected = { selectedColor ->
                    viewModel.onAction(Action.ChangeColor(dialogState.itemState.counterId, selectedColor))
                },
                onDismiss = { viewModel.onAction(Action.CloseCounterEditDialog(true)) },
                onConfirm = { viewModel.onAction(Action.CloseCounterEditDialog()) }
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
        modifier = Modifier.shadow(4.dp),
        actions = {
            IconButton(
                onClick = onRemoveModeSwitch,
                modifier = Modifier.size(40.dp),
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
                    modifier = Modifier.size(24.dp)
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
    LazyVerticalGrid(
        columns = GridCells.Adaptive(150.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(
            items = counterItems,
            key = { it.counterId },
        ) { counterItem ->
            CounterItem(
                state = counterItem,
                removeMode = removeMode,
                onPLusClick = { onAction(Action.PlusClick(counterItem.counterId)) },
                onMinusClick = { onAction(Action.MinusClick(counterItem.counterId)) },
                onEditClick = { onAction(Action.OpenCounterEditDialog(counterItem.counterId)) },
                onInputTitle = { onAction(Action.TitleInput(counterItem.counterId, it)) },
                onInputTitleDone = { onAction(Action.TitleInputDone(counterItem.counterId)) },
                onInputValue = { onAction(Action.ValueInput(counterItem.counterId, it)) },
                onInputValueDone = { onAction(Action.ValueInputDone(counterItem.counterId)) },
                onRemoveClick = { onAction(Action.RemoveClick(counterItem.counterId)) },
                modifier = Modifier.padding(top = 12.dp).animateItem()
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun CounterTopAppBarPreview() {
    JustCounterTheme {
        Column {
            CounterListTopAppBar(true, {})
            CounterListTopAppBar(false, {})
        }

    }
}

@Composable
@Preview(showBackground = true)
fun CounterScreenFABPreview() {
    JustCounterTheme {
        CounterListFAB({}, true)
    }
}
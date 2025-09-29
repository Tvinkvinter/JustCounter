package com.atarusov.justcounter.features.counters_screen.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.atarusov.justcounter.features.counters_screen.presentation.edit_counter_dialog.EditCounterDialog
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.Action
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterListScreenViewModel
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.OneTimeEvent
import com.atarusov.justcounter.ui.CounterItem
import com.atarusov.justcounter.ui.theme.JustCounterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterListScreen(viewModel: CounterListScreenViewModel = hiltViewModel()) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val titleEmptyErrorMessage = stringResource(R.string.counter_screen_empty_title_error)
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.screenEvents.collect { event ->
            when (event) {
                OneTimeEvent.ClearFocus -> focusManager.clearFocus(force = true)
                is OneTimeEvent.ShowTitleInputError -> {
                    Toast.makeText(context, titleEmptyErrorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test") },
                modifier = Modifier.shadow(4.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                ),
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAction(Action.CreateNewCounter) }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_plus_48),
                    contentDescription = stringResource(R.string.counter_screen_add_btn_description)
                )
            }
        },
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            itemsIndexed(
                items = state.counterItems,
                key = { index, counterItem -> counterItem.counterId },
                contentType = { index, counterItem -> CounterItem::class }
            ) { index, counterItem ->
                CounterItem(
                    state = counterItem,
                    onPLusClick = { viewModel.onAction(Action.CounterPlusClick(it)) },
                    onMinusClick = { viewModel.onAction(Action.CounterMinusClick(it)) },
                    onEditClick = {
                        viewModel.onAction(
                            Action.OpenCounterEditDialog(it)
                        )
                    },
                    onInputTitle = {
                        viewModel.onAction(Action.CounterTitleInput(counterItem.counterId, it))
                    },
                    onInputTitleDone = {
                        viewModel.onAction(Action.CounterTitleInputDone(counterItem.counterId))
                    },
                    onInputValue = {
                        viewModel.onAction(
                            Action.CounterValueInput(counterItem.counterId, it)
                        )
                    },
                    onInputValueDone = {
                        viewModel.onAction(
                            Action.CounterValueInputDone(counterItem.counterId)
                        )
                    },
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }

    state.editDialog?.let { editDialogState ->
        EditCounterDialog(
            state = editDialogState,
            events = viewModel.screenEvents,
            onTitleInput = { newTitle ->
                viewModel.onAction(
                    Action.CounterTitleInput(
                        counterId = editDialogState.itemState.counterId,
                        newTitle = newTitle
                    )
                )
            },
            onTitleInputDone = { counterId ->
                viewModel.onAction(
                    Action.CounterTitleInputDone(counterId)
                )
            },
            onValueInput = { newValue ->
                viewModel.onAction(
                    Action.CounterValueInput(
                        counterId = editDialogState.itemState.counterId,
                        newValue = newValue
                    )
                )
            },
            onColorSelected = { selectedColor ->
                viewModel.onAction(
                    Action.CounterChangeColor(
                        counterId = editDialogState.itemState.counterId,
                        newColor = selectedColor
                    )
                )
            },
            onDismiss = { viewModel.onAction(Action.CloseCounterEditDialog(true)) },
            onConfirm = { viewModel.onAction(Action.CloseCounterEditDialog()) }
        )
    }
}


@Composable
@Preview(showBackground = true)
fun CounterScreenPreview() {
    JustCounterTheme {
        CounterListScreen()
    }
}
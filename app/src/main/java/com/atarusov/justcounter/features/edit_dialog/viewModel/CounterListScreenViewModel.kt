package com.atarusov.justcounter.features.edit_dialog.viewModel

import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.atarusov.justcounter.features.edit_dialog.mvi.Actor
import com.atarusov.justcounter.features.edit_dialog.mvi.OneTimeEventHandler
import com.atarusov.justcounter.features.edit_dialog.mvi.Reducer
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.Action
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.State
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.StepConfiguratorState
import com.atarusov.justcounter.navigation.EditCounterDialogRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditCounterDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val actor: Actor,
    private val reducer: Reducer,
    private val oneTimeEventHandler: OneTimeEventHandler,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<EditCounterDialogRoute>()
    private val initialState = State(
        TextFieldValue(route.title),
        TextFieldValue(route.value.toString()),
        route.color,
        StepConfiguratorState(route.steps.map { TextFieldValue(it.toString()) }, route.color),
        route.counterId
    )

    private val _screenEvents = MutableSharedFlow<OneTimeEvent>()
    val screenEvents: SharedFlow<OneTimeEvent> = _screenEvents.asSharedFlow()

    private val _screenState = MutableStateFlow(initialState)
    val screenState: StateFlow<State> = _screenState.asStateFlow()

    fun onAction(action: Action) {
        viewModelScope.launch {
            actor.handleAction(action).collect { internalAction ->
                _screenState.update { previousState ->
                    reducer.reduce(previousState, internalAction)
                }
                oneTimeEventHandler.handleEvent(internalAction)?.let { _screenEvents.emit(it) }
            }
        }
    }
}
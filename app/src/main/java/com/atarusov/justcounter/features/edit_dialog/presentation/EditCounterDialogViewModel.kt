package com.atarusov.justcounter.features.edit_dialog.presentation

import android.os.SystemClock
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.Actor
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.OneTimeEventHandler
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.Reducer
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.State
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.StepConfiguratorState
import com.atarusov.justcounter.navigation.EditCounterDialogRoute
import com.atarusov.justcounter.shared_features.analytics.AnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class EditCounterDialogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val actor: Actor,
    private val reducer: Reducer,
    private val oneTimeEventHandler: OneTimeEventHandler,
    private val analyticsTracker: AnalyticsTracker,
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

    private val openedAtMs = SystemClock.elapsedRealtime()
    private var closeEventLogged = false

    fun onAction(action: Action) {
        viewModelScope.launch {
            actor.handleAction(action).collect { internalAction ->
                _screenState.update { previousState ->
                    reducer.reduce(previousState, internalAction)
                }
                if (
                    internalAction is InternalAction.CloseEditCounterDialog &&
                    action is Action.CloseCounterEditDialog
                ) {
                    logCloseEvent(action.state, action.saveChanges)
                }
                oneTimeEventHandler.handleEvent(internalAction)?.let { _screenEvents.emit(it) }
            }
        }
    }

    override fun onCleared() {
        onDialogDismissed()
        super.onCleared()
    }

    fun onDialogDismissed() {
        logCloseEvent(_screenState.value, saved = false)
    }

    private fun logCloseEvent(state: State, saved: Boolean) {
        if (closeEventLogged) return
        closeEventLogged = true

        analyticsTracker.logCounterEditClosed(
            saved = saved,
            changedTitle = state.titleField.text != initialState.titleField.text,
            changedValue = state.valueField.text != initialState.valueField.text,
            changedColor = state.color != initialState.color,
            changedSteps = state.stepConfiguratorState.steps.map { it.text } !=
                initialState.stepConfiguratorState.steps.map { it.text },
            durationSec = TimeUnit.MILLISECONDS.toSeconds(
                (SystemClock.elapsedRealtime() - openedAtMs).coerceAtLeast(0L)
            ),
        )
    }
}

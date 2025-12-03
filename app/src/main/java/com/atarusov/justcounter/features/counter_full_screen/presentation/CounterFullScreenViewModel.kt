package com.atarusov.justcounter.features.counter_full_screen.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.Actor
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.Bootstrapper
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.OneTimeEventHandler
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.Reducer
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.State
import com.atarusov.justcounter.navigation.CounterFullScreen
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
class CounterFullScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val actor: Actor,
    private val reducer: Reducer,
    private val oneTimeEventHandler: OneTimeEventHandler,
    private val bootstrapper: Bootstrapper
) : ViewModel() {

    private val route = savedStateHandle.toRoute<CounterFullScreen>()
    private val initialCounterState = with(route) {
        Counter(title, value, color, steps, id = counterId)
    }

    private val _screenEvents = MutableSharedFlow<OneTimeEvent>()
    val screenEvents: SharedFlow<OneTimeEvent> = _screenEvents.asSharedFlow()

    private val _screenState = MutableStateFlow(State(false, initialCounterState))
    val screenState: StateFlow<State> = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            bootstrapper.bootstrap(initialCounterState.id).collect { internalAction ->
                _screenState.update { previousState ->
                    reducer.reduce(previousState, internalAction)
                }
            }
        }
    }

    fun onAction(action: Action) {
        viewModelScope.launch {
            actor.handleAction(action).collect { internalAction ->
                _screenState.update { previousState ->
                    reducer.reduce(previousState, internalAction)
                }

                oneTimeEventHandler.handleEvent(internalAction)?.let {
                    _screenEvents.emit(it)
                }
            }
        }
    }
}
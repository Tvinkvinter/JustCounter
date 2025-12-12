package com.atarusov.justcounter.features.counter_list_screen.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.Actor
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.Bootstrapper
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.OneTimeEventHandler
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.Reducer
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CounterListScreenViewModel @Inject constructor(
    private val actor: Actor,
    private val reducer: Reducer,
    private val oneTimeEventHandler: OneTimeEventHandler,
    private val bootstrapper: Bootstrapper
) : ViewModel() {

    private val _screenEvents = MutableSharedFlow<OneTimeEvent>()
    val screenEvents: SharedFlow<OneTimeEvent> = _screenEvents.asSharedFlow()

    private val initialState = State()

    private val _screenState = MutableStateFlow(initialState)
    val screenState: StateFlow<State> = _screenState.asStateFlow()

    private val _categoryIds = screenState.map { it.categoryId }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = initialState.categoryId
    )

    init {
        viewModelScope.launch {
            bootstrapper.bootstrap(_categoryIds)
                .collect { internalAction ->
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
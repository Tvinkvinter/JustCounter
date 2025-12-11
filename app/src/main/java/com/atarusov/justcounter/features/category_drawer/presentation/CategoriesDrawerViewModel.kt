package com.atarusov.justcounter.features.category_drawer.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.Actor
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.Bootstrapper
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.OneTimeEventHandler
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.Reducer
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.State
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
class CategoriesDrawerViewModel @Inject constructor(
    private val actor: Actor,
    private val reducer: Reducer,
    private val oneTimeEventHandler: OneTimeEventHandler,
    private val bootstrapper: Bootstrapper
) : ViewModel() {

    private val _screenEvents = MutableSharedFlow<OneTimeEvent>()
    val screenEvents: SharedFlow<OneTimeEvent> = _screenEvents.asSharedFlow()

    private val _screenState = MutableStateFlow(State(null))
    val screenState: StateFlow<State> = _screenState.asStateFlow()

    init {
        viewModelScope.launch {
            bootstrapper.bootstrap().collect { internalAction ->
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
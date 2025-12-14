package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.State
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is InternalAction.LoadData -> when (previousState) {
                State.Loading -> State.Loaded(
                    removeMode = false,
                    categoryName = internalAction.data.categoryName,
                    counter = internalAction.data.counter
                )
                is State.Loaded -> previousState.copy(
                    categoryName = internalAction.data.categoryName,
                    counter = internalAction.data.counter
                )
            }

            is InternalAction.UpdateCounterValue -> when (previousState) {
                State.Loading -> previousState
                is State.Loaded -> previousState.copy(
                    counter = previousState.counter.copy(value = internalAction.newValue)
                )
            }

            InternalAction.SwitchRemoveMode -> when (previousState) {
                State.Loading -> previousState
                is State.Loaded -> previousState.copy(removeMode = !previousState.removeMode)
            }

            InternalAction.NavigateBack,
            is InternalAction.OpenEditCounterDialog -> previousState
        }
}
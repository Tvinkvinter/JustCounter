package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.State
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is InternalAction.LoadCounter -> previousState.copy(counter = internalAction.counter)
            is InternalAction.UpdateCounterValue -> previousState.copy(
                counter = previousState.counter.copy(value = internalAction.newValue)
            )
            InternalAction.SwitchRemoveMode -> previousState.copy(removeMode = !previousState.removeMode)

            InternalAction.NavigateBack,
            is InternalAction.OpenEditCounterDialog -> previousState
        }

}
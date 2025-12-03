package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            InternalAction.NavigateBack -> OneTimeEvent.NavigateBack
            is InternalAction.OpenEditCounterDialog ->
                OneTimeEvent.OpenEditCounterDialog(internalAction.counter)

            is InternalAction.LoadCounter,
            is InternalAction.UpdateCounterValue,
            InternalAction.SwitchRemoveMode -> null

        }
    }
}
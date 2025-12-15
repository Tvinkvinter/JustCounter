package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction.*
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            NavigateBack -> OneTimeEvent.NavigateBack
            is OpenEditCounterDialog -> OneTimeEvent.OpenEditCounterDialog(internalAction.counter)

            is LoadData,
            is UpdateCounterValue,
            SwitchRemoveMode -> null
        }
    }
}
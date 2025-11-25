package com.atarusov.justcounter.features.counter_list_screen.mvi

import com.atarusov.justcounter.features.counter_list_screen.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_list_screen.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            InternalAction.ShowDragTip -> OneTimeEvent.ShowDragTip
            InternalAction.ScrollDown -> OneTimeEvent.ScrollDown
            is InternalAction.OpenEditCounterDialog ->
                OneTimeEvent.OpenEditCounterDialog(internalAction.counter)

            is InternalAction.LoadCounters,
            is InternalAction.AddCounter,
            is InternalAction.RemoveCounter,
            is InternalAction.UpdateCounterValue,
            is InternalAction.SwapCounters,
            InternalAction.SwitchRemoveMode -> null
        }
    }
}
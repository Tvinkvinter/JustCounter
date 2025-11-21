package com.atarusov.justcounter.features.counters_screen.mvi

import com.atarusov.justcounter.features.counters_screen.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            InternalAction.ShowDragTip -> OneTimeEvent.ShowDragTip
            InternalAction.ClearFocus -> OneTimeEvent.ClearFocus
            InternalAction.ScrollDown -> OneTimeEvent.ScrollDown
            is InternalAction.OpenEditCounterDialog -> OneTimeEvent.OpenEditCounterDialog(internalAction.counterId)

            is InternalAction.LoadCounterItems,
            is InternalAction.AddCounterItem,
            is InternalAction.RemoveCounterItem,
            is InternalAction.UpdateCounterItemValueField,
            is InternalAction.SwapCounters,
            InternalAction.SwitchRemoveMode -> null
        }
    }
}
package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.OneTimeEvent.ShowEmptyTitleTip
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            InternalAction.ClearFocus -> OneTimeEvent.ClearFocus
            InternalAction.ScrollDown -> OneTimeEvent.ScrollDown
            InternalAction.ShowDragTip -> OneTimeEvent.ShowDragTip
            InternalAction.ShowEmptyTitleTip -> ShowEmptyTitleTip

            is InternalAction.LoadCounterItems,
            is InternalAction.AddCounterItem,
            is InternalAction.RemoveCounterItem,
            is InternalAction.UpdateCounterItemColor,
            is InternalAction.UpdateCounterItemTitleField,
            is InternalAction.UpdateCounterItemValueField,
            is InternalAction.UpdateCounterItemSteps,
            is InternalAction.UpdateStepConfiguratorField,
            is InternalAction.RestoreCounterItem,
            InternalAction.RemoveLastStepField,
            InternalAction.AddStepField,
            is InternalAction.SwapCounters,
            InternalAction.SwitchRemoveMode,
            is InternalAction.OpenEditCounterDialog,
            InternalAction.CloseEditCounterDialog -> null
        }
    }
}
package com.atarusov.justcounter.features.edit_dialog.mvi

import com.atarusov.justcounter.features.edit_dialog.mvi.entities.InternalAction
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            InternalAction.ClearFocus -> OneTimeEvent.ClearFocus
            InternalAction.CloseEditCounterDialog -> OneTimeEvent.CloseEditDialog
            InternalAction.ShowEmptyTitleTip -> OneTimeEvent.ShowEmptyTitleTip

            is InternalAction.LoadCounterItem,
            is InternalAction.UpdateCounterItemTitleField,
            is InternalAction.UpdateCounterItemValueField,
            is InternalAction.UpdateStepConfiguratorField,
            InternalAction.RemoveLastStepField,
            InternalAction.AddStepField,
            is InternalAction.UpdateCounterItemColor -> null
        }
    }
}
package com.atarusov.justcounter.features.edit_dialog.presentation.mvi

import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.InternalAction.*
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            ClearFocus -> OneTimeEvent.ClearFocus
            CloseEditCounterDialog -> OneTimeEvent.CloseEditDialog
            ShowEmptyTitleTip -> OneTimeEvent.ShowEmptyTitleTip

            is LoadCounterItem,
            is UpdateCounterItemTitleField,
            is UpdateCounterItemValueField,
            is UpdateStepConfiguratorField,
            RemoveLastStepField,
            AddStepField,
            is UpdateCounterItemColor -> null
        }
    }
}
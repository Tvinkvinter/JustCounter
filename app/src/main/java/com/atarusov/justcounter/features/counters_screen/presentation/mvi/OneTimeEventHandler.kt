package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.OneTimeEvent.ShowTitleInputError
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            is InternalAction.ShowTitleError -> ShowTitleInputError(internalAction.counterId)
            InternalAction.ClearFocus -> OneTimeEvent.ClearFocus

            is InternalAction.LoadCounterItems,
            is InternalAction.AddCounterItem,
            is InternalAction.RemoveCounterItem,
            is InternalAction.UpdateCounterItem,
            is InternalAction.UpdateCounterItemTitleField,
            is InternalAction.UpdateCounterItemValueField,
            is InternalAction.OpenEditCounterDialog,
            InternalAction.CloseEditCounterDialog,
            InternalAction.SwitchRemoveMode -> null
        }
    }
}
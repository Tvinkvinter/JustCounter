package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi

import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.InternalAction.*
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            is ChangeCategory -> OneTimeEvent.ChangeCategory(internalAction.categoryId)
            ShowDragTip -> OneTimeEvent.ShowDragTip
            ScrollDown -> OneTimeEvent.ScrollDown
            is NavigateToCounterFullScreen ->
                OneTimeEvent.NavigateToCounterFullScreen(internalAction.counter)
            is OpenEditCounterDialog -> OneTimeEvent.OpenEditCounterDialog(internalAction.counter)

            is LoadData,
            is AddCounter,
            is RemoveCounter,
            is UpdateCounterValue,
            is SwapCounters,
            SwitchRemoveMode -> null
        }
    }
}
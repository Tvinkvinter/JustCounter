package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.model.CountersOfCategory

sealed class InternalAction {
    data class LoadData(val countersOfCategory: CountersOfCategory) : InternalAction()
    data class AddCounter(val counter: Counter) : InternalAction()
    data class RemoveCounter(val counterId: String) : InternalAction()
    data class UpdateCounterValue(val counterId: String, val newValue: Int) : InternalAction()
    data class SwapCounters(val fromIndex: Int, val toIndex: Int) : InternalAction()

    data class ChangeCategory(val categoryId: Int?): InternalAction()
    data object SwitchRemoveMode : InternalAction()
    data object ShowDragTip: InternalAction()
    data object ScrollDown : InternalAction()
    data class NavigateToCounterFullScreen(val counter: Counter) : InternalAction()
    data class OpenEditCounterDialog(val counter: Counter) : InternalAction()
}
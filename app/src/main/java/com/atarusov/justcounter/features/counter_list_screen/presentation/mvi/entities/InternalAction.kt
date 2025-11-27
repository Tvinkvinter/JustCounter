package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter

sealed class InternalAction {
    data class LoadCounters(val counters: List<Counter>) : InternalAction()
    data class AddCounter(val counter: Counter) : InternalAction()
    data class RemoveCounter(val counterId: String) : InternalAction()
    data class UpdateCounterValue(val counterId: String, val newValue: Int) : InternalAction()
    data class SwapCounters(val fromIndex: Int, val toIndex: Int) : InternalAction()
    data object ShowDragTip: InternalAction()

    data object SwitchRemoveMode : InternalAction()
    data object ScrollDown : InternalAction()
    data class OpenEditCounterDialog(val counter: Counter) : InternalAction()
}
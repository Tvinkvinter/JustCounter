package com.atarusov.justcounter.features.counters_screen.mvi.entities

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.domain.Counter

sealed class InternalAction {
    data class LoadCounterItems(val counters: List<Counter>) : InternalAction()
    data class AddCounterItem(val counter: Counter) : InternalAction()
    data class RemoveCounterItem(val counterId: String) : InternalAction()
    data class UpdateCounterItemValueField(val counterId: String, val newTextField: TextFieldValue) : InternalAction()
    data class SwapCounters(val fromIndex: Int, val toIndex: Int) : InternalAction()
    data object ShowDragTip: InternalAction()

    data object SwitchRemoveMode : InternalAction()
    data object ClearFocus : InternalAction()
    data object ScrollDown : InternalAction()
    data class OpenEditCounterDialog(val counterId: String) : InternalAction()
}
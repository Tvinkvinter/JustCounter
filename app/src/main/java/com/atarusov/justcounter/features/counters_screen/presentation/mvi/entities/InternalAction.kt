package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.ui.theme.CounterColor

sealed class InternalAction {
    data class LoadCounterItems(val counters: List<Counter>) : InternalAction()
    data class AddCounterItem(val counter: Counter) : InternalAction()
    data class RemoveCounterItem(val counterId: String) : InternalAction()
    data class UpdateCounterItemColor(val counterId: String, val newColor: CounterColor) : InternalAction()
    data class UpdateCounterItemTitleField(val counterId: String, val newTextField: TextFieldValue) : InternalAction()
    data class UpdateCounterItemValueField(val counterId: String, val newTextField: TextFieldValue) : InternalAction()
    data class UpdateCounterItemSteps(val counterId: String, val steps: List<Int>) : InternalAction()
    data class UpdateStepConfiguratorField(val stepIndex: Int, val newTextField: TextFieldValue) : InternalAction()
    data class RestoreCounterItem(val counterItem: CounterItem) : InternalAction()
    data object RemoveLastStepField : InternalAction()
    data object AddStepField : InternalAction()

    data class SwapCounters(val fromIndex: Int, val toIndex: Int) : InternalAction()
    data object SwitchRemoveMode : InternalAction()
    data class OpenEditCounterDialog(val counterId: String) : InternalAction()
    data object CloseEditCounterDialog : InternalAction()

    data object ClearFocus : InternalAction()
    data object ShowDragTip: InternalAction()
    data object ShowEmptyTitleTip : InternalAction()
}
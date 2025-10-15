package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

import com.atarusov.justcounter.features.counters_screen.domain.Counter

sealed class InternalAction {
    data class LoadCounterItems(val counters: List<Counter>) : InternalAction()
    data class AddCounterItem(val counter: Counter) : InternalAction()
    data class RemoveCounterItem(val counterId: String) : InternalAction()
    data class UpdateCounterItem(val counter: Counter) : InternalAction()
    data class UpdateCounterItemValueField(val counterId: String, val newFieldValue: String) : InternalAction()

    data class OpenEditCounterDialog(val counter: Counter) : InternalAction()
    data object CloseEditCounterDialog : InternalAction()
    data object SwitchRemoveMode : InternalAction()

    data class ShowTitleError(val counterId: String) : InternalAction()
    data object ClearFocus : InternalAction()
}
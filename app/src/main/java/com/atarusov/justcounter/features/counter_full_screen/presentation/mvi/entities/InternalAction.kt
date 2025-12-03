package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter

sealed class InternalAction {
    data class LoadCounter(val counter: Counter) : InternalAction()
    data class UpdateCounterValue(val counterId: String, val newValue: Int) : InternalAction()

    data object SwitchRemoveMode : InternalAction()
    data object NavigateBack : InternalAction()
    data class OpenEditCounterDialog(val counter: Counter) : InternalAction()
}
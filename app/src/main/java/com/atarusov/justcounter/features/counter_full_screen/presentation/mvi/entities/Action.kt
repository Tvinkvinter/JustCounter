package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter

sealed class Action {
    data class RemoveCounter(val counter: Counter) : Action()
    data class MinusClick(
        val counterId: String,
        val oldValue: Int,
        val step: Int,
        val hasCustomSteps: Boolean,
    ) : Action()
    data class PlusClick(
        val counterId: String,
        val oldValue: Int,
        val step: Int,
        val hasCustomSteps: Boolean,
    ) : Action()

    data object BackPressed : Action()
    data class SwitchRemoveMode(val enabled: Boolean) : Action()
    data class OpenCounterEditDialog(val counter: Counter) : Action()
}

package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter

sealed class Action {
    data class RemoveCounter(val counterId: String) : Action()
    data class MinusClick(val counterId: String, val oldValue: Int, val step: Int) : Action()
    data class PlusClick(val counterId: String, val oldValue: Int, val step: Int) : Action()

    data object BackPressed : Action()
    data object SwitchRemoveMode : Action()
    data class OpenCounterEditDialog(val counter: Counter) : Action()
}
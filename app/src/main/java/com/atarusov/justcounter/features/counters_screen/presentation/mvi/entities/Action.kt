package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

import androidx.compose.ui.graphics.Color
import com.atarusov.justcounter.features.counters_screen.domain.Counter

sealed class Action {
    data object GetAllCounters : Action()
    data object AddCounter : Action()
    data class RemoveCounter(val counterId: String) : Action()

    data class ChangeColor(val counterId: String, val newColor: Color) : Action()
    data class MinusClick(val counterId: String) : Action()
    data class PlusClick(val counterId: String) : Action()
    data class TitleInput(val counterId: String, val input: String) : Action()
    data class TitleInputDone(val counterId: String, val input: String) : Action()
    data class ValueInput(val counterId: String, val input: String) : Action()
    data class ValueInputDone(val counterId: String, val input: String) : Action()

    data object SwitchRemoveMode : Action()
    data class OpenCounterEditDialog(val counterId: String) : Action()
    data class CloseCounterEditDialog(
        val currentItem: CounterItem,
        val counterToRestore: Counter? = null
    ) : Action()
}
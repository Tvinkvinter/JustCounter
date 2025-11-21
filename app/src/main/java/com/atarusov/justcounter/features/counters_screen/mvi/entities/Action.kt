package com.atarusov.justcounter.features.counters_screen.mvi.entities

import androidx.compose.ui.text.input.TextFieldValue

sealed class Action {
    data object AddCounter : Action()
    data class RemoveCounter(val counterId: String) : Action()
    data class SwapCounters(val firstIndex: Int, val secondIndex: Int) : Action()
    data class MinusClick(val counterId: String, val step: Int, val valueField: TextFieldValue) : Action()
    data class PlusClick(val counterId: String, val step: Int, val valueField: TextFieldValue) : Action()
    data class ValueInput(val counterId: String, val inputField: TextFieldValue) : Action()
    data class ValueInputDone(val counterId: String, val input: String) : Action()
    object TitleTap : Action()

    data object SwitchRemoveMode : Action()
    data class OpenCounterEditDialog(val counterId: String) : Action()
}
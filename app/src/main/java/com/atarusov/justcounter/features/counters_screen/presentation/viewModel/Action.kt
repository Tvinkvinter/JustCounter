package com.atarusov.justcounter.features.counters_screen.presentation.viewModel

import androidx.compose.ui.graphics.Color

sealed class Action {
    data object CreateNewCounter : Action()
    data class CounterPlusClick(val counterId: String) : Action()
    data class CounterMinusClick(val counterId: String) : Action()
    data class CounterTitleInput(val counterId: String, val newTitle: String) : Action()
    data class CounterTitleInputDone(val counterId: String) : Action()
    data class CounterValueInput(val counterId: String, val newValue: String) : Action()
    data class CounterValueInputDone(val counterId: String) : Action()
    data class CounterChangeColor(val counterId: String, val newColor: Color) : Action()
    data class OpenCounterEditDialog(val counterId: String) : Action()
    data class CloseCounterEditDialog(val cancelEdits: Boolean = false) : Action()
}
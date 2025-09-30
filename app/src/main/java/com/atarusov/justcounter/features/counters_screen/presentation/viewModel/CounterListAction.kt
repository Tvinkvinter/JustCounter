package com.atarusov.justcounter.features.counters_screen.presentation.viewModel

import androidx.compose.ui.graphics.Color

sealed class CounterListAction {
    data object CreateNewCounter : CounterListAction()
    data class PlusClick(val counterId: String) : CounterListAction()
    data class MinusClick(val counterId: String) : CounterListAction()
    data class TitleInput(val counterId: String, val newTitle: String) : CounterListAction()
    data class TitleInputDone(val counterId: String) : CounterListAction()
    data class ValueInput(val counterId: String, val newValue: String) : CounterListAction()
    data class ValueInputDone(val counterId: String) : CounterListAction()
    data class ChangeColor(val counterId: String, val newColor: Color) : CounterListAction()
    data class OpenCounterEditDialog(val counterId: String) : CounterListAction()
    data class CloseCounterEditDialog(val cancelEdits: Boolean = false) : CounterListAction()
}